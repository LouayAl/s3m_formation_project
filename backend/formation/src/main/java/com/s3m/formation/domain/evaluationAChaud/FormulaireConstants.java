package com.s3m.formation.domain.evaluationAChaud;

import java.util.List;

public class FormulaireConstants {

    public record Question(int id, int sectionId, String fr, String en, String ar) {}
    public record Section(int id, String fr, String en, String ar) {}

    public static final List<Section> SECTIONS = List.of(
            new Section(1,
                    "Conditions de réalisation",
                    "Implementation Conditions",
                    "ظروف التنفيذ"),
            new Section(2,
                    "Compétences techniques et pédagogiques",
                    "Technical and Pedagogical Skills",
                    "الكفاءات التقنية والبيداغوجية"),
            new Section(3,
                    "Atteinte des objectifs",
                    "Achievement of Objectives",
                    "تحقيق الأهداف")
    );

    public static final List<Question> QUESTIONS = List.of(
            // Section 1
            new Question(1,  1,
                    "L'information concernant la formation a été complète",
                    "The information about the training was complete",
                    "كانت المعلومات المتعلقة بالتكوين كاملة"),
            new Question(2,  1,
                    "La durée et le rythme de la formation étaient conformes à ce qui a été annoncé",
                    "The duration and pace of the training matched what was announced",
                    "كانت مدة التكوين وإيقاعه متوافقين مع ما تم الإعلان عنه"),
            new Question(3,  1,
                    "Les documents annoncés ont été remis aux participants.",
                    "The announced documents were provided to participants.",
                    "تم تسليم الوثائق المُعلنة للمشاركين"),
            new Question(4,  1,
                    "Les documents remis constituent une aide à l'assimilation des contenus",
                    "The provided documents help in assimilating the content",
                    "الوثائق المُسلَّمة تُساعد على استيعاب المحتوى"),
            new Question(5,  1,
                    "Les contenus de la formation étaient adaptés à mon niveau initial",
                    "The training content was adapted to my initial level",
                    "كانت محتويات التكوين ملائمة لمستواي الأولي"),
            new Question(6,  1,
                    "Les conditions matérielles (locaux, restauration, facilité d'accès, etc.) étaient satisfaisantes.",
                    "The material conditions (premises, catering, accessibility, etc.) were satisfactory.",
                    "كانت الظروف المادية (المباني، التغذية، سهولة الوصول...) مُرضية"),
            // Section 2
            new Question(7,  2,
                    "Le formateur dispose des compétences techniques nécessaires",
                    "The trainer has the necessary technical skills",
                    "يمتلك المكوِّن الكفاءات التقنية اللازمة"),
            new Question(8,  2,
                    "Le formateur dispose des compétences pédagogiques",
                    "The trainer has the necessary pedagogical skills",
                    "يمتلك المكوِّن الكفاءات البيداغوجية اللازمة"),
            new Question(9,  2,
                    "Le formateur a su créer ou entretenir une ambiance agréable dans le groupe en formation",
                    "The trainer was able to create or maintain a pleasant atmosphere in the training group",
                    "استطاع المكوِّن خلق أو الحفاظ على جو ملائم داخل مجموعة التكوين"),
            new Question(10, 2,
                    "Les moyens pédagogiques étaient adaptés au contenu de la formation",
                    "The pedagogical resources were suited to the training content",
                    "كانت الوسائل البيداغوجية ملائمة لمحتوى التكوين"),
            // Section 3
            new Question(11, 3,
                    "Les objectifs de la formation correspondent à mes besoins professionnels",
                    "The training objectives match my professional needs",
                    "تتوافق أهداف التكوين مع احتياجاتي المهنية"),
            new Question(12, 3,
                    "Les objectifs recherchés à travers cette formation ont été atteint",
                    "The objectives sought through this training have been achieved",
                    "تم تحقيق الأهداف المنشودة من خلال هذا التكوين"),
            new Question(13, 3,
                    "D'une manière générale, cette formation me permettra d'améliorer mes compétences professionnelles",
                    "In general, this training will allow me to improve my professional skills",
                    "بشكل عام، سيُمكّنني هذا التكوين من تحسين كفاءاتي المهنية")
    );

    public static final java.util.Map<String, String[]> SCALE_LABELS = java.util.Map.of(
            "fr", new String[]{"Pas du tout", "Peu", "Moyen", "Tout à fait"},
            "en", new String[]{"Not at all", "A little", "Average", "Completely"},
            "ar", new String[]{"أبدًا", "قليلاً", "متوسط", "تمامًا"}
    );
}