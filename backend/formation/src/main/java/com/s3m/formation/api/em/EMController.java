    package com.s3m.formation.api.em;

    import com.s3m.formation.api.dto.*;
    import com.s3m.formation.domain.critereTemplate.CritereTemplateType;
    import com.s3m.formation.domain.critereTemplate.CritereTemplateService;

    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.Map;

    @RestController
    @RequestMapping("/api/em")
    @RequiredArgsConstructor
    @CrossOrigin(origins = "*")
    public class EMController {

        private final EMService emService;
        private final CritereTemplateService critereTemplateService;

        // GET /api/em/dashboard
        @GetMapping("/dashboard")
        @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR')")
        public EMDashboardKpiDto getDashboard() {
            return emService.getDashboardKpis();
        }

        // GET /api/em/sessions/{sessionId}/evaluations
        @GetMapping("/sessions/{sessionId}/evaluations")
        @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR')")
        public List<EvaluationDto> getSessionEvaluations(@PathVariable Integer sessionId) {
            return emService.getEvaluationsForSession(sessionId);
        }

        // GET /api/em/sessions/{sessionId}/participants/{employeId}/evaluations
        @GetMapping("/sessions/{sessionId}/participants/{employeId}/evaluations")
        @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR')")
        public List<EvaluationDto> getParticipantEvaluations(
                @PathVariable Integer sessionId,
                @PathVariable Integer employeId
        ) {
            return emService.getEvaluationsForParticipant(sessionId, employeId);
        }

        // GET /api/em/sessions/{sessionId}/stats
        @GetMapping("/sessions/{sessionId}/stats")
        @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR')")
        public List<Map<String, Object>> getSessionStats(@PathVariable Integer sessionId) {
            return emService.getParticipantStats(sessionId);
        }

        // POST /api/em/evaluations  — create or update (upsert)
        @PostMapping("/evaluations")
        @ResponseStatus(HttpStatus.CREATED)
        @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER')")
        public EvaluationDto saveEvaluation(@RequestBody EvaluationRequest request) {
            return emService.saveEvaluation(request);
        }

        // GET /api/em/sessions/{sessionId}/days/{jour}/criteres
        @GetMapping("/sessions/{sessionId}/days/{jour}/criteres")
        @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR')")
        public List<SessionCritereDto> getCriteres(
                @PathVariable Integer sessionId,
                @PathVariable Integer jour
        ) {
            return emService.getCriteres(sessionId, jour);
        }

        // POST /api/em/sessions/{sessionId}/days/{jour}/criteres
        @PostMapping("/sessions/{sessionId}/days/{jour}/criteres")
        @PreAuthorize("hasAnyAuthority('ADMIN','EQUIPMENT_MANAGER')")
        public List<SessionCritereDto> saveCriteres(
                @PathVariable Integer sessionId,
                @PathVariable Integer jour,
                @RequestBody SessionCritereRequest request
        ) {
            return emService.saveCriteres(sessionId, jour, request);
        }

        @GetMapping("/sessions/{sessionId}/days/{jour}/program")
        @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR')")
        public DailyProgramDto getDailyProgram(
                @PathVariable Integer sessionId,
                @PathVariable Integer jour
        ) {
            return emService.getDailyProgram(sessionId, jour);
        }

        @PostMapping("/sessions/{sessionId}/days/{jour}/program")
        @PreAuthorize("hasAnyAuthority('ADMIN','EQUIPMENT_MANAGER','TRAINER')")
        public DailyProgramDto saveDailyProgram(
                @PathVariable Integer sessionId,
                @PathVariable Integer jour,
                @RequestBody DailyProgramRequest request
        ) {
            return emService.saveDailyProgram(sessionId, jour, request);
        }

        // GET /api/em/sessions
        @GetMapping("/sessions")
        @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR')")
        public List<SessionFormationResponseDto> getSessionsForCurrentUser() {
            return emService.getSessionsForCurrentUser();
        }

        // GET /api/em/sessions/{sessionId}
        @GetMapping("/sessions/{sessionId}")
        @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR')")
        public SessionFormationResponseDto getSessionForCurrentUser(@PathVariable Integer sessionId) {
            return emService.getSessionForCurrentUser(sessionId);
        }

        @GetMapping("/employes")
        @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR')")
        public List<EmployeResponseDto> getEmployesForCurrentUser() {
            return emService.getEmployesForCurrentUser();
        }

        // GET /api/em/sessions/my — trainer's assigned sessions only
        @GetMapping("/sessions/my")
        @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR')")
        public List<SessionFormationResponseDto> getMySessionsAsTrainer() {
            return emService.getSessionsForTrainer();
        }

        // GET /api/em/formations — returns only formations linked to the caller's entreprise
        @GetMapping("/formations")
        @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR')")
        public List<FormationResponseDto> getFormationsForCurrentUser() {
            return emService.getFormationsForCurrentUser();
        }

        @GetMapping("/critere-templates")
        @PreAuthorize("hasAnyAuthority('ADMIN','EQUIPMENT_MANAGER')")
        public List<CritereTemplateDto> listCritereTemplates(
                @RequestParam(required = false) CritereTemplateType type
        ) {
            return critereTemplateService.listTemplates(type);
        }

        @PostMapping("/critere-templates")
        @ResponseStatus(HttpStatus.CREATED)
        @PreAuthorize("hasAnyAuthority('ADMIN','EQUIPMENT_MANAGER')")
        public CritereTemplateDto createCritereTemplate(@RequestBody CreateCritereTemplateRequest request) {
            return critereTemplateService.createTemplate(request);
        }

        @DeleteMapping("/critere-templates/{templateId}")
        @PreAuthorize("hasAnyAuthority('ADMIN','EQUIPMENT_MANAGER')")
        public void deleteCritereTemplate(@PathVariable Integer templateId) {
            critereTemplateService.deleteTemplate(templateId);
        }

        @GetMapping("/critere-templates/{templateId}/entries")
        @PreAuthorize("hasAnyAuthority('ADMIN','EQUIPMENT_MANAGER')")
        public List<CritereTemplateEntryDto> getCritereTemplateEntries(@PathVariable Integer templateId) {
            return critereTemplateService.getTemplateEntries(templateId);
        }

        @PutMapping("/critere-templates/{templateId}/entries")
        @PreAuthorize("hasAnyAuthority('ADMIN','EQUIPMENT_MANAGER')")
        public List<CritereTemplateEntryDto> saveCritereTemplateEntries(
                @PathVariable Integer templateId,
                @RequestBody CritereTemplateEntriesRequest request
        ) {
            return critereTemplateService.saveTemplateEntries(templateId, request);
        }

        @PostMapping("/sessions/{sessionId}/criteres/clone-from-template")
        @PreAuthorize("hasAnyAuthority('ADMIN','EQUIPMENT_MANAGER')")
        public void cloneCriteresFromTemplate(
                @PathVariable Integer sessionId,
                @RequestParam Integer templateId
        ) {
            critereTemplateService.cloneIntoSession(sessionId, templateId);
        }

    }
