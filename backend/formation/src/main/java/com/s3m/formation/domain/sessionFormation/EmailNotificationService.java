package com.s3m.formation.domain.sessionFormation;

import com.s3m.formation.domain.formateur.Formateur;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void sendFormateurConfirmationRequest(SessionFormation session, Formateur formateur) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(formateur.getEmail());
        message.setSubject("Confirmation de présence — Session " + session.getReferenceSession());
        message.setText(buildBody(session, formateur));
        mailSender.send(message);
    }

    private String buildBody(SessionFormation session, Formateur formateur) {
        String entreprise = session.getEntreprise() != null
                ? session.getEntreprise().getNomEntreprise() : "N/A";
        String formation = session.getFormation() != null
                ? session.getFormation().getModule() : "N/A";

        return """
                Bonjour %s %s,

                Vous êtes assigné(e) à la session de formation suivante :

                Formation : %s
                Entreprise : %s
                Date début : %s
                Date fin : %s
                Lieu : %s

                Merci de bien vouloir confirmer votre présence en répondant à cet email ou en contactant l'administration.

                Cordialement,
                S3M Formation
                """.formatted(
                formateur.getNom(), formateur.getPrenom(),
                formation, entreprise,
                session.getDateDebut() != null ? session.getDateDebut().format(DATE_FMT) : "N/A",
                session.getDateFin() != null ? session.getDateFin().format(DATE_FMT) : "N/A",
                session.getLieu() != null ? session.getLieu() : "N/A"
        );
    }
}