package com.s3m.formation.domain.evaluationAChaud;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.s3m.formation.api.dto.*;
import com.s3m.formation.domain.employe.Employe;
import com.s3m.formation.domain.employe.EmployeRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class EvaluationAChaudService {

    private final EvaluationAChaudRepository repository;
    private final EvaluationReponseRepository reponseRepository;
    private final SessionFormationRepository sessionRepository;
    private final EmployeRepository employeRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void submit(EvaluationAChaudRequest req) {
        if (repository.existsBySession_IdSessionAndEmploye_IdEmploye(
                req.idSession(), req.idEmploye())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Vous avez déjà soumis une évaluation pour cette session.");
        }

        SessionFormation session = sessionRepository.findById(req.idSession())
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
        Employe employe = employeRepository.findById(req.idEmploye())
                .orElseThrow(() -> new EntityNotFoundException("Employé not found"));

        EvaluationAChaud eval = EvaluationAChaud.builder()
                .session(session)
                .employe(employe)
                .jourEvaluation(null)   // no longer used
                .commentaire(req.commentaire())
                .soumisLe(LocalDateTime.now())
                .build();

        EvaluationAChaud saved = repository.save(eval);

        req.reponses().forEach((questionId, score) -> {
            EvaluationReponse reponse = EvaluationReponse.builder()
                    .evalChaud(saved)
                    .idQuestion(questionId)
                    .score(score)
                    .build();
            reponseRepository.save(reponse);
        });
    }

    @Transactional(readOnly = true)
    public EvaluationAChaudStatsDto getStats(Integer sessionId) {
        SessionFormation session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        int totalParticipants = session.getParticipations() != null
                ? session.getParticipations().size() : 0;

        String formateurNom = formateurNomComplet(session);

        List<EvaluationAChaud> allEvals = repository.findBySession_IdSession(sessionId);

        if (allEvals.isEmpty()) {
            return new EvaluationAChaudStatsDto(
                    sessionId, session.getReferenceSession(), session.getFormation().getModule(),
                    formateurNom,
                    0, totalParticipants, 0, Map.of(), List.of()
            );
        }

        // Aggregate all scores across all evaluations
        Map<Integer, List<Integer>> scoresByQuestion = new HashMap<>();
        for (EvaluationAChaud eval : allEvals) {
            if (eval.getReponses() != null) {
                for (EvaluationReponse r : eval.getReponses()) {
                    scoresByQuestion
                            .computeIfAbsent(r.getIdQuestion(), k -> new ArrayList<>())
                            .add(r.getScore());
                }
            }
        }

        Map<Integer, Double> moyennesParQuestion = new LinkedHashMap<>();
        for (int qId = 1; qId <= 13; qId++) {
            List<Integer> scores = scoresByQuestion.getOrDefault(qId, List.of());
            double avg = scores.isEmpty() ? 0
                    : round(scores.stream().mapToInt(i -> i).average().orElse(0));
            moyennesParQuestion.put(qId, avg);
        }

        double moyenneGlobale = round(
                moyennesParQuestion.values().stream()
                        .mapToDouble(Double::doubleValue)
                        .average().orElse(0)
        );

        List<EvaluationAChaudResponseDto> reponses = allEvals.stream()
                .map(this::toDto)
                .toList();

        return new EvaluationAChaudStatsDto(
                sessionId, session.getReferenceSession(), session.getFormation().getModule(),
                formateurNom,
                allEvals.size(), totalParticipants, moyenneGlobale,
                moyennesParQuestion, reponses
        );
    }

    private double round(double val) {
        return Math.round(val * 10.0) / 10.0;
    }

    private String formateurNomComplet(SessionFormation session) {
        return session.getFormateur() != null
                ? session.getFormateur().getNom() + " " + session.getFormateur().getPrenom()
                : "—";
    }

    private EvaluationAChaudResponseDto toDto(EvaluationAChaud e) {
        List<EvaluationReponseDto> reponseDtos = e.getReponses() != null
                ? e.getReponses().stream()
                .map(r -> new EvaluationReponseDto(r.getIdQuestion(), r.getScore()))
                .sorted(Comparator.comparing(EvaluationReponseDto::idQuestion))
                .toList()
                : List.of();

        return new EvaluationAChaudResponseDto(
                e.getIdEvalChaud(),
                e.getSession().getIdSession(),
                e.getEmploye().getNom() + " " + e.getEmploye().getPrenom(),
                e.getJourEvaluation(), // will be null, kept for DTO compatibility
                reponseDtos,
                e.getCommentaire(),
                e.getSoumisLe()
        );
    }

    @Transactional(readOnly = true)
    public List<EvaluationSummaryDto> getSummaryForEntreprise(Integer entrepriseId) {
        return sessionRepository.findByEntreprise_IdEntreprise(entrepriseId)
                .stream()
                .map(session -> {
                    List<EvaluationAChaud> evals =
                            repository.findBySession_IdSession(session.getIdSession());

                    double moyenneGlobale = 0;
                    LocalDateTime derniere = null;

                    if (!evals.isEmpty()) {
                        List<Integer> allScores = evals.stream()
                                .filter(e -> e.getReponses() != null)
                                .flatMap(e -> e.getReponses().stream())
                                .map(EvaluationReponse::getScore)
                                .toList();
                        if (!allScores.isEmpty()) {
                            moyenneGlobale = Math.round(
                                    allScores.stream().mapToInt(i -> i).average().orElse(0) * 10.0
                            ) / 10.0;
                        }
                        derniere = evals.stream()
                                .map(EvaluationAChaud::getSoumisLe)
                                .filter(Objects::nonNull)
                                .max(LocalDateTime::compareTo)
                                .orElse(null);
                    }

                    return new EvaluationSummaryDto(
                            session.getIdSession(),
                            session.getReferenceSession(),
                            session.getFormation() != null
                                    ? session.getFormation().getModule() : "",
                            formateurNomComplet(session),
                            evals.size(),
                            session.getParticipations() != null
                                    ? session.getParticipations().size() : 0,
                            moyenneGlobale,
                            derniere
                    );
                })
                .filter(s -> s.totalReponses() > 0)
                .toList();
    }

    @Transactional(readOnly = true)
    public SatisfactionKpiDto getSatisfactionKpis(Integer sessionId) {
        SessionFormation session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        Integer entrepriseId = session.getEntreprise().getIdEntreprise();
        Integer formationId  = session.getFormation().getIdFormation();

        double s3m       = round(repository.findAvgScoreGlobalS3M().orElse(0.0));
        double client    = round(repository.findAvgScoreByEntreprise(entrepriseId).orElse(0.0));
        double formation = round(repository.findAvgScoreByEntrepriseAndFormation(
                entrepriseId, formationId).orElse(0.0));
        double sessionAvg = round(repository.findAvgScoreBySession(sessionId).orElse(0.0));

        return new SatisfactionKpiDto(
                s3m, client, formation, sessionAvg,
                repository.countReponsesGlobalS3M(),
                repository.countReponsesForEntreprise(entrepriseId),
                repository.countReponsesForEntrepriseAndFormation(entrepriseId, formationId),
                repository.countReponsesForSession(sessionId)
        );
    }

    /* =========================================================
       EXPORT — EXCEL
       ========================================================= */

    @Transactional(readOnly = true)
    public byte[] exportExcel(Integer sessionId) {
        SessionFormation session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
        EvaluationAChaudStatsDto stats = getStats(sessionId);

        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            CellStyle headerStyle = excelHeaderStyle(wb);
            CellStyle titleStyle  = excelTitleStyle(wb);
            CellStyle wrapStyle   = excelWrapStyle(wb);
            CellStyle centerStyle = excelCenterStyle(wb);

            // ---- Sheet 1: Résumé ----
            Sheet summary = wb.createSheet("Résumé");
            int r = 0;

            Row titleRow = summary.createRow(r++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Rapport d'évaluation à chaud — " + stats.moduleFormation());
            titleCell.setCellStyle(titleStyle);
            r++;

            r = writeKeyValueRow(summary, r, "Référence session", stats.referenceSession());
            r = writeKeyValueRow(summary, r, "Formation", stats.moduleFormation());
            r = writeKeyValueRow(summary, r, "Formateur", stats.formateur());
            r = writeKeyValueRow(summary, r, "Entreprise",
                    session.getEntreprise() != null ? session.getEntreprise().getNomEntreprise() : "—");
            r = writeKeyValueRow(summary, r, "Dates",
                    formatDateRange(session.getDateDebut(), session.getDateFin()));
            r = writeKeyValueRow(summary, r, "Lieu", session.getLieu() != null ? session.getLieu() : "—");
            r = writeKeyValueRow(summary, r, "Participants", String.valueOf(stats.totalParticipants()));
            r = writeKeyValueRow(summary, r, "Réponses reçues", String.valueOf(stats.totalReponses()));
            r = writeKeyValueRow(summary, r, "Moyenne globale", stats.moyenneGlobale() + " / 4");
            r++;

            // Per-question averages table
            Row qHeader = summary.createRow(r++);
            String[] qCols = {"Question", "Section", "Moyenne /4", "Nb réponses"};
            for (int c = 0; c < qCols.length; c++) {
                Cell cell = qHeader.createCell(c);
                cell.setCellValue(qCols[c]);
                cell.setCellStyle(headerStyle);
            }

            for (FormulaireConstants.Question q : FormulaireConstants.QUESTIONS) {
                Row row = summary.createRow(r++);
                row.createCell(0).setCellValue(q.fr());
                row.createCell(1).setCellValue(sectionLabel(q.sectionId()));
                Double avg = stats.moyennesParQuestion().get(q.id());
                Cell avgCell = row.createCell(2);
                avgCell.setCellValue(avg != null ? avg : 0);
                avgCell.setCellStyle(centerStyle);
                long nbReponses = stats.reponses().stream()
                        .filter(resp -> resp.reponses().stream().anyMatch(rep -> rep.idQuestion() == q.id()))
                        .count();
                Cell nbCell = row.createCell(3);
                nbCell.setCellValue(nbReponses);
                nbCell.setCellStyle(centerStyle);
            }

            for (int c = 0; c < qCols.length; c++) summary.autoSizeColumn(c);
            summary.setColumnWidth(0, 14000);

            // ---- Sheet 2: Réponses détaillées ----
            Sheet detail = wb.createSheet("Réponses détaillées");
            int dr = 0;

            Row detailHeader = detail.createRow(dr++);
            List<String> headerCols = new ArrayList<>();
            headerCols.add("Participant");
            headerCols.add("Soumis le");
            for (FormulaireConstants.Question q : FormulaireConstants.QUESTIONS) {
                headerCols.add("Q" + q.id());
            }
            headerCols.add("Commentaire");

            for (int c = 0; c < headerCols.size(); c++) {
                Cell cell = detailHeader.createCell(c);
                cell.setCellValue(headerCols.get(c));
                cell.setCellStyle(headerStyle);
            }

            for (EvaluationAChaudResponseDto resp : stats.reponses()) {
                Row row = detail.createRow(dr++);
                row.createCell(0).setCellValue(resp.nomEmploye());
                row.createCell(1).setCellValue(
                        resp.soumisLe() != null ? resp.soumisLe().format(DATETIME_FMT) : "—");

                int col = 2;
                for (FormulaireConstants.Question q : FormulaireConstants.QUESTIONS) {
                    Integer score = resp.reponses().stream()
                            .filter(rep -> rep.idQuestion() == q.id())
                            .map(EvaluationReponseDto::score)
                            .findFirst().orElse(null);
                    Cell cell = row.createCell(col++);
                    if (score != null) cell.setCellValue(score);
                    cell.setCellStyle(centerStyle);
                }

                Cell commentCell = row.createCell(col);
                commentCell.setCellValue(resp.commentaire() != null ? resp.commentaire() : "");
                commentCell.setCellStyle(wrapStyle);
            }

            for (int c = 0; c < headerCols.size() - 1; c++) detail.autoSizeColumn(c);
            detail.setColumnWidth(headerCols.size() - 1, 12000);

            // ---- Sheet 3: Légende questions ----
            Sheet legend = wb.createSheet("Légende questions");
            int lr = 0;
            Row legendHeader = legend.createRow(lr++);
            String[] legendCols = {"Code", "Section", "Question"};
            for (int c = 0; c < legendCols.length; c++) {
                Cell cell = legendHeader.createCell(c);
                cell.setCellValue(legendCols[c]);
                cell.setCellStyle(headerStyle);
            }
            for (FormulaireConstants.Question q : FormulaireConstants.QUESTIONS) {
                Row row = legend.createRow(lr++);
                row.createCell(0).setCellValue("Q" + q.id());
                row.createCell(1).setCellValue(sectionLabel(q.sectionId()));
                Cell qCell = row.createCell(2);
                qCell.setCellValue(q.fr());
                qCell.setCellStyle(wrapStyle);
            }
            legend.setColumnWidth(2, 18000);
            legend.autoSizeColumn(0);
            legend.autoSizeColumn(1);

            wb.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération du fichier Excel", e);
        }
    }

    private int writeKeyValueRow(Sheet sheet, int rowIndex, String key, String value) {
        Row row = sheet.createRow(rowIndex);
        Cell keyCell = row.createCell(0);
        keyCell.setCellValue(key);
        org.apache.poi.ss.usermodel.Font bold = sheet.getWorkbook().createFont();
        bold.setBold(true);
        CellStyle boldStyle = sheet.getWorkbook().createCellStyle();
        boldStyle.setFont(bold);
        keyCell.setCellStyle(boldStyle);
        row.createCell(1).setCellValue(value != null ? value : "—");
        return rowIndex + 1;
    }

    private String sectionLabel(int sectionId) {
        return FormulaireConstants.SECTIONS.stream()
                .filter(s -> s.id() == sectionId)
                .map(FormulaireConstants.Section::fr)
                .findFirst().orElse("—");
    }

    private String formatDateRange(java.time.LocalDate debut, java.time.LocalDate fin) {
        if (debut == null || fin == null) return "—";
        return debut.format(DATE_FMT) + " — " + fin.format(DATE_FMT);
    }

    private CellStyle excelHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle excelTitleStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        return style;
    }

    private CellStyle excelWrapStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setWrapText(true);
        style.setVerticalAlignment(VerticalAlignment.TOP);
        return style;
    }

    private CellStyle excelCenterStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    /* =========================================================
       EXPORT — PDF
       ========================================================= */

    @Transactional(readOnly = true)
    public byte[] exportPdf(Integer sessionId, String barChartImageBase64) {
        SessionFormation session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
        EvaluationAChaudStatsDto stats = getStats(sessionId);
        SatisfactionKpiDto kpis = getSatisfactionKpis(sessionId);

        Color brandDark  = new Color(154, 157, 158);
        Color brandBlue  = new Color(25, 118, 210);
        Color brandGreen = new Color(102, 187, 106);
        Color rowTint    = new Color(230, 236, 245);
        Color brandOrange = new Color(255, 152, 0);


        Font titleFont     = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, Color.WHITE);
        Font subtitleFont  = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.WHITE);
        Font labelFont     = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.DARK_GRAY);
        Font valueFont     = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
        Font kpiValueFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, Color.WHITE);
        Font kpiLabelFont  = FontFactory.getFont(FontFactory.HELVETICA, 7, Color.WHITE);
        Font tableHeader   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7, Color.WHITE);
        Font tableBody     = FontFactory.getFont(FontFactory.HELVETICA, 7, Color.BLACK);
        Font moyenneFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7, brandDark);
        Font smallGray     = FontFactory.getFont(FontFactory.HELVETICA, 7, Color.GRAY);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4.rotate(), 24, 24, 18, 18);
            PdfWriter.getInstance(doc, out);
            doc.open();

            // ---- Header band with logo ----
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{5f, 1f});

            PdfPCell headerTextCell = new PdfPCell();
            headerTextCell.setBackgroundColor(brandDark);
            headerTextCell.setPadding(10);
            headerTextCell.setBorder(Rectangle.NO_BORDER);
            headerTextCell.addElement(new Paragraph("Rapport d'évaluation à chaud", titleFont));
            headerTextCell.addElement(new Paragraph(stats.moduleFormation(), subtitleFont));
            headerTable.addCell(headerTextCell);

            PdfPCell logoCell = new PdfPCell();
            logoCell.setBackgroundColor(brandDark);
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            try (var logoStream = getClass().getResourceAsStream("/static/logo.png")) {
                if (logoStream != null) {
                    Image logo = Image.getInstance(logoStream.readAllBytes());
                    logo.scaleToFit(100, 46);
                    logoCell.addElement(logo);
                }
            } catch (Exception ignored) {
                // logo missing/unreadable — report still generates without it
            }
            headerTable.addCell(logoCell);
            doc.add(headerTable);
            doc.add(new Paragraph(" ", valueFont));

            // ---- Compact session info row ----
            PdfPTable infoTable = new PdfPTable(6);
            infoTable.setWidthPercentage(100);
            addCompactInfo(infoTable, "Référence", stats.referenceSession(), labelFont, valueFont);
            addCompactInfo(infoTable, "Formateur", stats.formateur(), labelFont, valueFont);
            addCompactInfo(infoTable, "Entreprise",
                    session.getEntreprise() != null ? session.getEntreprise().getNomEntreprise() : "—",
                    labelFont, valueFont);
            addCompactInfo(infoTable, "Dates",
                    formatDateRange(session.getDateDebut(), session.getDateFin()), labelFont, valueFont);
            addCompactInfo(infoTable, "Lieu",
                    session.getLieu() != null ? session.getLieu() : "—", labelFont, valueFont);
            addCompactInfo(infoTable, "Réponses",
                    stats.totalReponses() + " / " + stats.totalParticipants(), labelFont, valueFont);
            doc.add(infoTable);
            doc.add(new Paragraph(" ", valueFont));

            // ---- KPI benchmark banner ----
            PdfPTable kpiTable = new PdfPTable(4);
            kpiTable.setWidthPercentage(100);
            addKpiCell(kpiTable, "Moyenne globale (session)", stats.moyenneGlobale(), brandDark, kpiValueFont, kpiLabelFont);
            addKpiCell(kpiTable, "Client (toutes formations)", kpis.satisfactionClientGlobale(), brandGreen, kpiValueFont, kpiLabelFont);
            addKpiCell(kpiTable, "Formation (client)", kpis.satisfactionParFormation(), brandOrange, kpiValueFont, kpiLabelFont);
            addKpiCell(kpiTable, "Cette session", kpis.satisfactionSession(), brandBlue, kpiValueFont, kpiLabelFont);
            doc.add(kpiTable);
            doc.add(new Paragraph(" ", valueFont));

            // ---- Score par question chart (rendered by frontend, embedded here) ----
            if (barChartImageBase64 != null && !barChartImageBase64.isBlank()) {
                try {
                    byte[] chartBytes = java.util.Base64.getDecoder().decode(barChartImageBase64);
                    Image chartImage = Image.getInstance(chartBytes);
                    chartImage.scaleToFit(760, 150);
                    chartImage.setAlignment(Element.ALIGN_CENTER);
                    doc.add(chartImage);
                    doc.add(new Paragraph(" ", valueFont));
                } catch (Exception ignored) {
                    // bad/missing image — report still generates without the chart
                }
            }

            // ---- Full detail grid: moyenne row + one row per participant ----
            int nbQuestions = FormulaireConstants.QUESTIONS.size();
            PdfPTable detailTable = new PdfPTable(nbQuestions + 1);
            detailTable.setWidthPercentage(100);
            float[] widths = new float[nbQuestions + 1];
            widths[0] = 3f;
            for (int i = 1; i <= nbQuestions; i++) widths[i] = 1f;
            detailTable.setWidths(widths);

            PdfPCell nameHeader = new PdfPCell(new Phrase("Participant", tableHeader));
            styleHeaderCell(nameHeader, brandBlue);
            detailTable.addCell(nameHeader);
            for (FormulaireConstants.Question q : FormulaireConstants.QUESTIONS) {
                PdfPCell qHead = new PdfPCell(new Phrase("Q" + q.id(), tableHeader));
                styleHeaderCell(qHead, brandBlue);
                detailTable.addCell(qHead);
            }

            PdfPCell moyenneLabelCell = new PdfPCell(new Phrase("Moyenne", moyenneFont));
            moyenneLabelCell.setBackgroundColor(rowTint);
            moyenneLabelCell.setPadding(4);
            detailTable.addCell(moyenneLabelCell);
            for (FormulaireConstants.Question q : FormulaireConstants.QUESTIONS) {
                Double avg = stats.moyennesParQuestion().get(q.id());
                PdfPCell avgCell = new PdfPCell(new Phrase(avg != null ? String.valueOf(avg) : "—", moyenneFont));
                avgCell.setBackgroundColor(rowTint);
                avgCell.setPadding(4);
                avgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                detailTable.addCell(avgCell);
            }

            for (EvaluationAChaudResponseDto resp : stats.reponses()) {
                PdfPCell nameCell = new PdfPCell(new Phrase(resp.nomEmploye(), tableBody));
                nameCell.setPadding(4);
                detailTable.addCell(nameCell);
                for (FormulaireConstants.Question q : FormulaireConstants.QUESTIONS) {
                    Integer score = resp.reponses().stream()
                            .filter(rep -> rep.idQuestion() == q.id())
                            .map(EvaluationReponseDto::score)
                            .findFirst().orElse(null);
                    PdfPCell scoreCell = new PdfPCell(
                            new Phrase(score != null ? String.valueOf(score) : "—", tableBody));
                    scoreCell.setPadding(4);
                    scoreCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    detailTable.addCell(scoreCell);
                }
            }
            doc.add(detailTable);

            // ---- Footer ----
            Paragraph footer = new Paragraph(
                    "Généré le " + LocalDateTime.now().format(DATETIME_FMT), smallGray);
            footer.setAlignment(Element.ALIGN_RIGHT);
            footer.setSpacingBefore(6);
            doc.add(footer);

            doc.close();
            return out.toByteArray();

        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    private void addCompactInfo(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(4);
        cell.addElement(new Paragraph(label, labelFont));
        cell.addElement(new Paragraph(value != null ? value : "—", valueFont));
        table.addCell(cell);
    }

    private void addKpiCell(PdfPTable table, String label, double score, Color bg, Font valueFont, Font labelFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(bg);
        cell.setPadding(8);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        Paragraph value = new Paragraph(score + " / 4", valueFont);
        value.setAlignment(Element.ALIGN_CENTER);
        Paragraph label2 = new Paragraph(label, labelFont);
        label2.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(value);
        cell.addElement(label2);
        table.addCell(cell);
    }
    private void addInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.BOTTOM);
        labelCell.setBorderColor(Color.LIGHT_GRAY);
        labelCell.setPadding(6);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "—", valueFont));
        valueCell.setBorder(Rectangle.BOTTOM);
        valueCell.setBorderColor(Color.LIGHT_GRAY);
        valueCell.setPadding(6);
        table.addCell(valueCell);
    }

    private void styleHeaderCell(PdfPCell cell, Color bg) {
        cell.setBackgroundColor(bg);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    }
}