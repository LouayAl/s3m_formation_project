--
-- PostgreSQL database dump
--

\restrict mOuVuBqwXCPiXZlJNilLVUyVElXDWawQ1XsRwhNnFSB0eR9jT8NDquKoA8QLrec

-- Dumped from database version 16.11 (Debian 16.11-1.pgdg13+1)
-- Dumped by pg_dump version 16.11 (Debian 16.11-1.pgdg13+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: check_session_statut_transition(); Type: FUNCTION; Schema: public; Owner: s3m_user
--

CREATE FUNCTION public.check_session_statut_transition() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- If statut didn't change → allow
    IF OLD.statut = NEW.statut THEN
        RETURN NEW;
    END IF;

    -- Allowed transitions
    IF OLD.statut = 'PLANIFIEE' AND NEW.statut = 'EN_COURS' THEN
        RETURN NEW;
    END IF;

    IF OLD.statut = 'EN_COURS' AND NEW.statut = 'TERMINEE' THEN
        RETURN NEW;
    END IF;

    IF NEW.statut = 'ANNULEE' THEN
        RETURN NEW;
    END IF;

    -- Otherwise → block
    RAISE EXCEPTION
        'Invalid session status transition: % → %',
        OLD.statut, NEW.statut;
END;
$$;


ALTER FUNCTION public.check_session_statut_transition() OWNER TO s3m_user;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: cout_formation; Type: TABLE; Schema: public; Owner: s3m_user
--

CREATE TABLE public.cout_formation (
    id_cout integer NOT NULL,
    id_session integer NOT NULL,
    remboursement character varying(100),
    prix_heure_mad numeric(10,2),
    prix_jour_mad numeric(10,2),
    autres_depenses numeric(10,2),
    cout_total numeric(10,2) NOT NULL
);


ALTER TABLE public.cout_formation OWNER TO s3m_user;

--
-- Name: cout_formation_id_cout_seq; Type: SEQUENCE; Schema: public; Owner: s3m_user
--

CREATE SEQUENCE public.cout_formation_id_cout_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.cout_formation_id_cout_seq OWNER TO s3m_user;

--
-- Name: cout_formation_id_cout_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: s3m_user
--

ALTER SEQUENCE public.cout_formation_id_cout_seq OWNED BY public.cout_formation.id_cout;


--
-- Name: demande_reservation; Type: TABLE; Schema: public; Owner: s3m_user
--

CREATE TABLE public.demande_reservation (
    id_demande integer NOT NULL,
    id_entreprise integer NOT NULL,
    id_formation integer NOT NULL,
    id_fiche integer NOT NULL,
    date_debut_souhaitee date,
    date_fin_souhaitee date,
    statut character varying(30) DEFAULT 'REQUESTED'::character varying NOT NULL,
    commentaire_client text,
    commentaire_admin text,
    date_creation timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.demande_reservation OWNER TO s3m_user;

--
-- Name: demande_reservation_id_demande_seq; Type: SEQUENCE; Schema: public; Owner: s3m_user
--

CREATE SEQUENCE public.demande_reservation_id_demande_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.demande_reservation_id_demande_seq OWNER TO s3m_user;

--
-- Name: demande_reservation_id_demande_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: s3m_user
--

ALTER SEQUENCE public.demande_reservation_id_demande_seq OWNED BY public.demande_reservation.id_demande;


--
-- Name: departement; Type: TABLE; Schema: public; Owner: s3m_user
--

CREATE TABLE public.departement (
    id_departement integer NOT NULL,
    nom character varying(255) NOT NULL,
    id_entreprise integer NOT NULL
);


ALTER TABLE public.departement OWNER TO s3m_user;

--
-- Name: departement_id_departement_seq; Type: SEQUENCE; Schema: public; Owner: s3m_user
--

CREATE SEQUENCE public.departement_id_departement_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.departement_id_departement_seq OWNER TO s3m_user;

--
-- Name: departement_id_departement_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: s3m_user
--

ALTER SEQUENCE public.departement_id_departement_seq OWNED BY public.departement.id_departement;


--
-- Name: employe; Type: TABLE; Schema: public; Owner: s3m_user
--

CREATE TABLE public.employe (
    id_employe integer NOT NULL,
    nom character varying(100) NOT NULL,
    prenom character varying(100) NOT NULL,
    cin character varying(50),
    cnss character varying(50),
    matricule character varying(50),
    f_h character(1),
    csp character varying(50),
    fonction character varying(100),
    type_contrat character varying(50),
    date_embauche date,
    date_naissance date,
    id_entreprise integer NOT NULL,
    id_manager integer,
    email character varying(255),
    telephone character varying(30),
    id_departement integer
);


ALTER TABLE public.employe OWNER TO s3m_user;

--
-- Name: employe_id_employe_seq; Type: SEQUENCE; Schema: public; Owner: s3m_user
--

CREATE SEQUENCE public.employe_id_employe_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.employe_id_employe_seq OWNER TO s3m_user;

--
-- Name: employe_id_employe_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: s3m_user
--

ALTER SEQUENCE public.employe_id_employe_seq OWNED BY public.employe.id_employe;


--
-- Name: entreprise; Type: TABLE; Schema: public; Owner: s3m_user
--

CREATE TABLE public.entreprise (
    id_entreprise integer NOT NULL,
    nom_entreprise character varying(255) NOT NULL
);


ALTER TABLE public.entreprise OWNER TO s3m_user;

--
-- Name: entreprise_id_entreprise_seq; Type: SEQUENCE; Schema: public; Owner: s3m_user
--

CREATE SEQUENCE public.entreprise_id_entreprise_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.entreprise_id_entreprise_seq OWNER TO s3m_user;

--
-- Name: entreprise_id_entreprise_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: s3m_user
--

ALTER SEQUENCE public.entreprise_id_entreprise_seq OWNED BY public.entreprise.id_entreprise;


--
-- Name: evaluation_a_chaud; Type: TABLE; Schema: public; Owner: s3m_user
--

CREATE TABLE public.evaluation_a_chaud (
    id_eval_chaud integer NOT NULL,
    id_participation integer NOT NULL,
    evaluation_a_chaud text
);


ALTER TABLE public.evaluation_a_chaud OWNER TO s3m_user;

--
-- Name: evaluation_a_chaud_id_eval_chaud_seq; Type: SEQUENCE; Schema: public; Owner: s3m_user
--

CREATE SEQUENCE public.evaluation_a_chaud_id_eval_chaud_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.evaluation_a_chaud_id_eval_chaud_seq OWNER TO s3m_user;

--
-- Name: evaluation_a_chaud_id_eval_chaud_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: s3m_user
--

ALTER SEQUENCE public.evaluation_a_chaud_id_eval_chaud_seq OWNED BY public.evaluation_a_chaud.id_eval_chaud;


--
-- Name: evaluation_a_froid; Type: TABLE; Schema: public; Owner: s3m_user
--

CREATE TABLE public.evaluation_a_froid (
    id_eval_froid integer NOT NULL,
    id_participation integer NOT NULL,
    id_n_plus_1 integer NOT NULL,
    evaluation_participant text,
    evaluation_n_plus_1 text,
    date_evaluation_a_froid date,
    taux_efficacite numeric(5,2)
);


ALTER TABLE public.evaluation_a_froid OWNER TO s3m_user;

--
-- Name: evaluation_a_froid_id_eval_froid_seq; Type: SEQUENCE; Schema: public; Owner: s3m_user
--

CREATE SEQUENCE public.evaluation_a_froid_id_eval_froid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.evaluation_a_froid_id_eval_froid_seq OWNER TO s3m_user;

--
-- Name: evaluation_a_froid_id_eval_froid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: s3m_user
--

ALTER SEQUENCE public.evaluation_a_froid_id_eval_froid_seq OWNED BY public.evaluation_a_froid.id_eval_froid;


--
-- Name: fiche_technique_formation; Type: TABLE; Schema: public; Owner: s3m_user
--

CREATE TABLE public.fiche_technique_formation (
    id_fiche integer NOT NULL,
    id_formation integer NOT NULL,
    version_numero integer NOT NULL,
    statut character varying(20) DEFAULT 'ACTIVE'::character varying NOT NULL,
    date_creation timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    date_activation date,
    date_archivage date,
    description text,
    objectifs text,
    competences_cible text,
    prerequis text,
    population_cible text,
    programme text,
    nb_participants_min integer,
    nb_participants_max integer,
    duree_jours numeric,
    duree_heures numeric,
    modalites_evaluation text,
    indicateurs_succes text
);


ALTER TABLE public.fiche_technique_formation OWNER TO s3m_user;

--
-- Name: fiche_technique_formation_id_fiche_seq; Type: SEQUENCE; Schema: public; Owner: s3m_user
--

CREATE SEQUENCE public.fiche_technique_formation_id_fiche_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.fiche_technique_formation_id_fiche_seq OWNER TO s3m_user;

--
-- Name: fiche_technique_formation_id_fiche_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: s3m_user
--

ALTER SEQUENCE public.fiche_technique_formation_id_fiche_seq OWNED BY public.fiche_technique_formation.id_fiche;


--
-- Name: formateur; Type: TABLE; Schema: public; Owner: s3m_user
--

CREATE TABLE public.formateur (
    id_formateur integer NOT NULL,
    nom character varying(150) NOT NULL,
    prenom character varying(150) NOT NULL,
    email character varying(255) NOT NULL,
    telephone character varying(30),
    id_fournisseur integer NOT NULL,
    actif boolean DEFAULT true
);


ALTER TABLE public.formateur OWNER TO s3m_user;

--
-- Name: formateur_id_formateur_seq; Type: SEQUENCE; Schema: public; Owner: s3m_user
--

CREATE SEQUENCE public.formateur_id_formateur_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.formateur_id_formateur_seq OWNER TO s3m_user;

--
-- Name: formateur_id_formateur_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: s3m_user
--

ALTER SEQUENCE public.formateur_id_formateur_seq OWNED BY public.formateur.id_formateur;


--
-- Name: formation; Type: TABLE; Schema: public; Owner: s3m_user
--

CREATE TABLE public.formation (
    id_formation integer NOT NULL,
    module character varying(255) NOT NULL,
    type_formation character varying(150) NOT NULL,
    famille_formation character varying(150) NOT NULL,
    sous_famille character varying(150),
    interne_externe character varying(50),
    annee integer,
    reference_formation character varying(50) NOT NULL,
    prix_heure_mad numeric(10,2),
    prix_jour_mad numeric(10,2),
    d_heures numeric(5,2),
    d_jours numeric(5,2)
);


ALTER TABLE public.formation OWNER TO s3m_user;

--
-- Name: formation_id_formation_seq; Type: SEQUENCE; Schema: public; Owner: s3m_user
--

CREATE SEQUENCE public.formation_id_formation_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.formation_id_formation_seq OWNER TO s3m_user;

--
-- Name: formation_id_formation_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: s3m_user
--

ALTER SEQUENCE public.formation_id_formation_seq OWNED BY public.formation.id_formation;


--
-- Name: participation; Type: TABLE; Schema: public; Owner: s3m_user
--

CREATE TABLE public.participation (
    id_participation integer NOT NULL,
    id_session integer NOT NULL,
    id_employe integer NOT NULL
);


ALTER TABLE public.participation OWNER TO s3m_user;

--
-- Name: participation_id_participation_seq; Type: SEQUENCE; Schema: public; Owner: s3m_user
--

CREATE SEQUENCE public.participation_id_participation_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.participation_id_participation_seq OWNER TO s3m_user;

--
-- Name: participation_id_participation_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: s3m_user
--

ALTER SEQUENCE public.participation_id_participation_seq OWNED BY public.participation.id_participation;


--
-- Name: session_formation; Type: TABLE; Schema: public; Owner: s3m_user
--

CREATE TABLE public.session_formation (
    id_session integer NOT NULL,
    id_formation integer NOT NULL,
    id_entreprise integer NOT NULL,
    date_debut date NOT NULL,
    date_fin date NOT NULL,
    d_heures numeric(5,2),
    d_jours numeric(5,2),
    formateur character varying(150),
    fournisseur character varying(150),
    id_formateur integer,
    id_fournisseur integer,
    statut character varying(30),
    id_demande integer,
    reference_session character varying(50)
);


ALTER TABLE public.session_formation OWNER TO s3m_user;

--
-- Name: session_formation_audit; Type: TABLE; Schema: public; Owner: s3m_user
--

CREATE TABLE public.session_formation_audit (
    id_audit bigint NOT NULL,
    id_session integer NOT NULL,
    statut_avant character varying(30) NOT NULL,
    statut_apres character varying(30) NOT NULL,
    modifie_par character varying(255) NOT NULL,
    date_modification timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    commentaire text
);


ALTER TABLE public.session_formation_audit OWNER TO s3m_user;

--
-- Name: session_formation_audit_id_audit_seq; Type: SEQUENCE; Schema: public; Owner: s3m_user
--

CREATE SEQUENCE public.session_formation_audit_id_audit_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.session_formation_audit_id_audit_seq OWNER TO s3m_user;

--
-- Name: session_formation_audit_id_audit_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: s3m_user
--

ALTER SEQUENCE public.session_formation_audit_id_audit_seq OWNED BY public.session_formation_audit.id_audit;


--
-- Name: session_formation_id_session_seq; Type: SEQUENCE; Schema: public; Owner: s3m_user
--

CREATE SEQUENCE public.session_formation_id_session_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.session_formation_id_session_seq OWNER TO s3m_user;

--
-- Name: session_formation_id_session_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: s3m_user
--

ALTER SEQUENCE public.session_formation_id_session_seq OWNED BY public.session_formation.id_session;


--
-- Name: users; Type: TABLE; Schema: public; Owner: s3m_user
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    email character varying(255) NOT NULL,
    password_hash character varying(255) NOT NULL,
    role character varying(50) DEFAULT 'USER'::character varying NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    id_entreprise integer,
    prenom character varying(50) NOT NULL,
    nom character varying(50) NOT NULL
);


ALTER TABLE public.users OWNER TO s3m_user;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: s3m_user
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_id_seq OWNER TO s3m_user;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: s3m_user
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: cout_formation id_cout; Type: DEFAULT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.cout_formation ALTER COLUMN id_cout SET DEFAULT nextval('public.cout_formation_id_cout_seq'::regclass);


--
-- Name: demande_reservation id_demande; Type: DEFAULT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.demande_reservation ALTER COLUMN id_demande SET DEFAULT nextval('public.demande_reservation_id_demande_seq'::regclass);


--
-- Name: departement id_departement; Type: DEFAULT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.departement ALTER COLUMN id_departement SET DEFAULT nextval('public.departement_id_departement_seq'::regclass);


--
-- Name: employe id_employe; Type: DEFAULT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.employe ALTER COLUMN id_employe SET DEFAULT nextval('public.employe_id_employe_seq'::regclass);


--
-- Name: entreprise id_entreprise; Type: DEFAULT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.entreprise ALTER COLUMN id_entreprise SET DEFAULT nextval('public.entreprise_id_entreprise_seq'::regclass);


--
-- Name: evaluation_a_chaud id_eval_chaud; Type: DEFAULT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.evaluation_a_chaud ALTER COLUMN id_eval_chaud SET DEFAULT nextval('public.evaluation_a_chaud_id_eval_chaud_seq'::regclass);


--
-- Name: evaluation_a_froid id_eval_froid; Type: DEFAULT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.evaluation_a_froid ALTER COLUMN id_eval_froid SET DEFAULT nextval('public.evaluation_a_froid_id_eval_froid_seq'::regclass);


--
-- Name: fiche_technique_formation id_fiche; Type: DEFAULT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.fiche_technique_formation ALTER COLUMN id_fiche SET DEFAULT nextval('public.fiche_technique_formation_id_fiche_seq'::regclass);


--
-- Name: formateur id_formateur; Type: DEFAULT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.formateur ALTER COLUMN id_formateur SET DEFAULT nextval('public.formateur_id_formateur_seq'::regclass);


--
-- Name: formation id_formation; Type: DEFAULT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.formation ALTER COLUMN id_formation SET DEFAULT nextval('public.formation_id_formation_seq'::regclass);


--
-- Name: participation id_participation; Type: DEFAULT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.participation ALTER COLUMN id_participation SET DEFAULT nextval('public.participation_id_participation_seq'::regclass);


--
-- Name: session_formation id_session; Type: DEFAULT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.session_formation ALTER COLUMN id_session SET DEFAULT nextval('public.session_formation_id_session_seq'::regclass);


--
-- Name: session_formation_audit id_audit; Type: DEFAULT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.session_formation_audit ALTER COLUMN id_audit SET DEFAULT nextval('public.session_formation_audit_id_audit_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Data for Name: cout_formation; Type: TABLE DATA; Schema: public; Owner: s3m_user
--

COPY public.cout_formation (id_cout, id_session, remboursement, prix_heure_mad, prix_jour_mad, autres_depenses, cout_total) FROM stdin;
1	2	CSF	900.00	7200.00	0.00	14400.00
2	3	CSF	900.00	7200.00	0.00	14400.00
3	4	CSF	900.00	7200.00	0.00	14400.00
4	5	CSF	900.00	7200.00	0.00	14400.00
5	10	CSF	900.00	7200.00	\N	28800.00
6	11	CSF	900.00	7200.00	\N	28800.00
7	22	CSF	900.00	7200.00	0.00	14400.00
8	22	Emergence	900.00	7200.00	0.00	14400.00
9	31	CSF	0.00	\N	\N	0.00
10	39	CSF	900.00	7200.00	1000.00	15400.00
11	39	Emergence	900.00	7200.00	1000.00	15400.00
12	40	CSF	900.00	7200.00	1000.00	15400.00
13	40	Emergence	900.00	7200.00	1000.00	15400.00
14	41	CSF	900.00	7200.00	1000.00	15400.00
15	43	CSF	900.00	7200.00	0.00	14400.00
16	44	CSF	900.00	7200.00	0.00	28800.00
17	45	CSF	900.00	7200.00	0.00	28800.00
18	58	CSF	900.00	7200.00	1500.00	30300.00
19	58	Emergence	900.00	7200.00	1500.00	30300.00
20	59	CSF	900.00	7200.00	1500.00	30300.00
21	59	Emergence	900.00	7200.00	1500.00	30300.00
22	60	Emergence	900.00	7200.00	1500.00	30300.00
23	61	CSF	900.00	7200.00	1500.00	30300.00
24	62	CSF	900.00	7200.00	1500.00	30300.00
25	63	CSF	900.00	7200.00	1500.00	30300.00
26	63	Emergence	900.00	7200.00	1500.00	30300.00
27	64	CSF	900.00	7200.00	1500.00	30300.00
28	64	Emergence	900.00	7200.00	1500.00	30300.00
29	65	Emergence	900.00	7200.00	1500.00	30300.00
30	66	CSF	900.00	7200.00	1500.00	30300.00
31	67	CSF	900.00	7200.00	1500.00	30300.00
32	95	CSF	900.00	7200.00	1000.00	15400.00
33	95	Emergence	900.00	7200.00	1000.00	15400.00
34	107	CSF	900.00	7200.00	\N	14400.00
35	117	Emergence	900.00	7200.00	1500.00	8700.00
36	117	Emergence	900.00	7200.00	1500.00	15900.00
37	118	Emergence	900.00	7200.00	1500.00	8700.00
38	118	Emergence	900.00	7200.00	1500.00	15900.00
39	120	CSF	900.00	7200.00	0.00	28800.00
40	132	CSF	900.00	7200.00	1000.00	15400.00
41	132	Emergence	900.00	7200.00	1000.00	15400.00
42	133	CSF	900.00	7200.00	1000.00	15400.00
43	134	CSF	900.00	7200.00	1000.00	15400.00
44	166	CSF	900.00	7200.00	1000.00	15400.00
45	166	Emergence	900.00	7200.00	1000.00	15400.00
46	167	CSF	900.00	7200.00	1000.00	15400.00
47	168	CSF	900.00	7200.00	1000.00	15400.00
48	197	CSF	900.00	7200.00	0.00	28800.00
49	198	CSF	900.00	7200.00	0.00	7200.00
50	199	CSF	900.00	7200.00	0.00	28800.00
51	200	CSF	900.00	7200.00	0.00	21600.00
52	201	CSF	900.00	7200.00	0.00	28800.00
53	202	CSF	900.00	7200.00	0.00	14400.00
54	202	CSF	900.00	7200.00	\N	14400.00
55	211	CSF	900.00	7200.00	1000.00	15400.00
56	211	Emergence	900.00	7200.00	1000.00	15400.00
57	212	CSF	900.00	7200.00	1000.00	15400.00
58	212	Emergence	900.00	7200.00	1000.00	15400.00
59	213	CSF	900.00	7200.00	1000.00	15400.00
60	224	CSF	900.00	7200.00	1500.00	30300.00
61	224	Emergence	900.00	7200.00	1500.00	30300.00
62	225	CSF	900.00	7200.00	1500.00	30300.00
63	225	Emergence	900.00	7200.00	1500.00	30300.00
64	226	Emergence	900.00	7200.00	1500.00	30300.00
65	227	CSF	900.00	7200.00	1500.00	30300.00
66	228	CSF	900.00	7200.00	1500.00	30300.00
67	233	CSF	900.00	7200.00	0.00	28800.00
68	234	CSF	900.00	7200.00	0.00	28800.00
69	243	CSF	900.00	7200.00	0.00	14400.00
70	243	CSF	900.00	7200.00	\N	14400.00
71	249	CSF	500.00	4000.00	200.00	8400.00
72	250	CSF	500.00	4000.00	250.00	8500.00
73	251	CSF	600.00	4800.00	300.00	9900.00
\.


--
-- Data for Name: demande_reservation; Type: TABLE DATA; Schema: public; Owner: s3m_user
--

COPY public.demande_reservation (id_demande, id_entreprise, id_formation, id_fiche, date_debut_souhaitee, date_fin_souhaitee, statut, commentaire_client, commentaire_admin, date_creation) FROM stdin;
5	1	5	8	2025-03-10	2025-03-14	VALIDEE	We want an onsite session for our team	\N	2025-12-24 14:21:14.851434
4	1	2	5	2025-07-07	2025-01-20	VALIDEE	\N	\N	2025-12-23 13:35:31.603175
6	12	5	8	2025-12-30	2025-12-31	VALIDEE	TEST commentaire	Test admin commentaire	2025-12-30 12:47:33.281091
7	12	5	8	2025-12-30	2025-12-31	VALIDEE	TEST Commentaire	test admin commentaire	2025-12-30 12:50:18.014564
8	12	5	8	2025-12-30	2025-12-31	VALIDEE	test commentaire	TEST admin Commentaire	2025-12-30 12:51:12.204849
\.


--
-- Data for Name: departement; Type: TABLE DATA; Schema: public; Owner: s3m_user
--

COPY public.departement (id_departement, nom, id_entreprise) FROM stdin;
1	O2CK	4
2	PEI	1
3	3W	8
4	MON	4
5	QCP	6
6	EMB	8
7	O2CK	6
8	EMB	7
9	FER	2
10	PEI	10
11	QCP	4
12	UTEE	8
13	CPL	2
14	O2CK	1
15	PEI	8
16	FER	1
17	MON	8
18	CPL	4
19	MON	2
20	UTEE	7
21	FER	7
22	MOT	1
23	FER	8
24	MON	1
25	EMB	4
26	MON	6
27	O2CK	2
28	QCP	3
29	EMB	2
30	QCP	8
31	F2M	8
32	RH	8
33	UTEE	5
34	CPL	8
35	QCP	2
36	PEI	7
37	O2X	8
38	PEI	4
39	PEI	2
40	UTEE	1
41	MOT	4
42	PEI	5
43	MOT	8
44	O2CK	8
45	F2M	4
46	UTEE	4
47	FER	6
48	O2CK	7
49	MOT	9
50	MOT	6
51	MOT	7
52	MOT	2
53	FER	4
\.


--
-- Data for Name: employe; Type: TABLE DATA; Schema: public; Owner: s3m_user
--

COPY public.employe (id_employe, nom, prenom, cin, cnss, matricule, f_h, csp, fonction, type_contrat, date_embauche, date_naissance, id_entreprise, id_manager, email, telephone, id_departement) FROM stdin;
2	AZE	YOUSSEF	\N	\N	6819	H	A	\N	\N	2021-12-31	1995-01-04	8	\N	\N	\N	30
416	ZAR	ABDELKBIR	\N	\N	31300	H	E	\N	\N	2024-09-30	1994-01-01	9	\N	\N	\N	49
417	ESS	AYOUB	\N	\N	31105	F	P	\N	\N	2024-09-04	2000-02-11	8	\N	\N	\N	44
418	EL	YOUSSEF	\N	\N	33493	N	A	\N	\N	2025-10-13	1994-06-26	4	\N	\N	\N	38
420	FAH	MOHAMMED	\N	\N	9725	H	A	\N	\N	2023-09-14	2001-01-04	8	\N	\N	\N	17
421	KHO	ACHRAF	\N	\N	32545	H	O	\N	\N	2025-06-02	1996-02-12	8	\N	\N	\N	34
422	ALL	YOUNESS	\N	\N	8677	H	O	\N	\N	2023-01-30	1990-07-13	8	\N	\N	\N	44
426	DAH	ABDELHAK	\N	\N	32767	H	A	\N	\N	2025-07-08	1997-08-11	8	\N	\N	\N	6
428	DRI	LAKHLOUFI	\N	\N	9790	H	A	\N	\N	2021-10-04	1978-01-21	8	\N	\N	\N	15
429	ABD	FATHI	\N	\N	5894	H	P	\N	\N	2021-08-26	1997-08-04	2	\N	\N	\N	19
430	EL	FOUAD	\N	\N	30663	H	A	\N	\N	2024-04-22	1989-01-09	1	\N	\N	\N	22
432	ZOU	ABDELOUHED	\N	\N	32746	H	O	\N	\N	2023-07-31	1989-12-20	8	\N	\N	\N	34
434	ZAH	YOUSSEF	\N	\N	33492	H	P	\N	\N	2025-10-27	1990-11-16	8	\N	\N	\N	17
435	BEN	AZZEDINE	\N	\N	32176	H	P	\N	\N	2025-05-12	1989-04-05	4	\N	\N	\N	53
436	AZE	MALIKA	\N	\N	7503	F	P	\N	\N	2020-09-09	1975-02-17	8	\N	\N	\N	31
437	AIT	Benachir	\N	\N	33018	H	P	\N	\N	2025-08-25	1995-09-01	8	\N	\N	\N	17
438	EL	Yassine	\N	\N	30738	H	E	\N	\N	2024-06-03	1995-01-16	8	\N	\N	\N	43
441	SAI	AWATIF	\N	\N	8114	N	A	\N	\N	2022-11-19	1991-07-15	8	\N	\N	\N	31
442	#REF!	ABDELBAR	\N	\N	30255	H	#REF!	\N	\N	2024-01-15	1996-05-11	4	\N	\N	\N	4
443	LAH	ABDELOUAHED	\N	\N	4572	H	O	\N	\N	2020-12-28	1980-01-19	8	\N	\N	\N	34
444	EL	OUAZINE	\N	\N	30837	H	E	\N	\N	2024-07-01	1984-06-18	7	\N	\N	\N	21
445	KHR	YOUNESS	\N	\N	6984	H	A	\N	\N	2022-02-13	1995-06-06	8	\N	\N	\N	15
446	EL	OMAR	\N	\N	5429	H	P	\N	\N	2021-04-24	1994-05-20	8	\N	\N	\N	15
447	LAH	YOUSSEF	\N	\N	9679	H	A	\N	\N	2023-09-04	2002-06-16	4	\N	\N	\N	38
449	EZ	KHALID	\N	\N	8244	H	A	\N	\N	2022-11-27	1998-04-18	8	\N	\N	\N	17
450	MAL	YOUSSEF	\N	\N	2783	N	A	\N	\N	2020-07-09	1984-01-20	8	\N	\N	\N	15
451	ES	YASSINE	\N	\N	31967	H	E	\N	\N	2025-01-16	1995-01-12	2	\N	\N	\N	35
452	BEL	CHARAF-EDDINE	\N	\N	1271	H	P	\N	\N	2019-07-22	1981-01-01	8	\N	\N	\N	23
453	DAH	FOUAD	\N	\N	5725	H	O	\N	\N	2021-06-28	1994-10-08	8	\N	\N	\N	17
455	CHE	MOHAMED	\N	\N	32889	H	A	\N	\N	2025-07-23	1998-10-20	8	\N	\N	\N	6
456	KAM	MAROUANE	\N	\N	32366	H	P	\N	\N	2025-06-02	1998-04-12	4	\N	\N	\N	41
457	KHA	HAMZA	\N	\N	32671	H	P	\N	\N	2025-08-04	1996-01-24	4	\N	\N	\N	53
458	ES-	MOHAMED	\N	\N	824	H	A	\N	\N	2019-05-16	1995-12-22	8	\N	\N	\N	23
459	AZA	FAHD	\N	\N	33284	H	O	\N	\N	2025-09-26	1999-11-20	8	\N	\N	\N	30
460	EL	HAJAR	\N	\N	31169	F	E	\N	\N	2024-09-19	2001-09-21	6	\N	\N	\N	50
461	DER	AYMANE	\N	\N	31639	H	A	\N	\N	2024-10-24	2000-03-27	8	\N	\N	\N	31
464	SAD	MUSTAPHA	\N	\N	2687	H	P	\N	\N	2020-07-02	1992-10-17	8	\N	\N	\N	17
465	EL	ANOUAR	\N	\N	1712	H	P	\N	\N	2019-10-24	1992-10-25	8	\N	\N	\N	23
466	ESS	EL MAHDI	\N	\N	32434	H	A	\N	\N	2025-05-20	2004-04-16	1	\N	\N	\N	22
467	EL-	Soufiane	\N	\N	30512	H	O	\N	\N	2024-03-04	1995-06-20	4	\N	\N	\N	1
469	BOU	ABDELHAFID	\N	\N	31803	H	P	\N	\N	2024-11-14	1997-03-22	8	\N	\N	\N	12
471	AZZ	SAAD	\N	\N	31134	H	P	\N	\N	2024-09-05	1994-05-22	8	\N	\N	\N	30
473	EL-	MOHAMMED	\N	\N	32515	H	A	\N	\N	2025-06-05	2002-05-29	8	\N	\N	\N	30
474	DAY	HASSAN	\N	\N	6792	H	O	\N	\N	2021-12-19	1997-09-02	8	\N	\N	\N	6
477	KAR	HAMZA	\N	\N	6639	H	P	\N	\N	2021-11-20	1994-08-21	8	\N	\N	\N	30
478	TIT	YAHYA	\N	\N	8247	H	O	\N	\N	2022-11-27	1996-08-15	8	\N	\N	\N	30
479	ELA	MONSIF	\N	\N	6989	H	O	\N	\N	2022-02-20	1994-08-18	8	\N	\N	\N	23
482	EN-	ABDELALI	\N	\N	7739	H	O	\N	\N	2022-10-06	1989-12-30	3	\N	\N	\N	28
484	TAS	IBRAHIM	\N	\N	6656	H	P	\N	\N	2021-11-20	1997-06-04	8	\N	\N	\N	30
485	SAI	YOUSSEF	\N	\N	2941	H	P	\N	\N	2020-08-31	1994-09-03	8	\N	\N	\N	17
486	ASS	MOHAMMED	\N	\N	33084	H	O	\N	\N	2025-08-10	1997-06-19	4	\N	\N	\N	53
487	EL	MOURAD	\N	\N	32112	H	P	\N	\N	\N	\N	4	\N	\N	\N	4
488	BEN	CHAIMAE	\N	\N	32667	F	A	\N	\N	2025-07-21	2001-08-06	8	\N	\N	\N	30
489	BOU	ABDELMOUNIM	\N	\N	33140	H	O	\N	\N	2025-08-25	2002-01-12	8	\N	\N	\N	30
492	TIF	Youness	\N	\N	33039	H	A	\N	\N	2025-09-08	1991-08-22	4	\N	\N	\N	1
494	EL-	JAMAL	\N	\N	32669	H	A	\N	\N	2025-08-04	2000-05-20	8	\N	\N	\N	17
495	Gue	Oussama	\N	\N	33047	H	P	\N	\N	2025-09-08	2001-12-31	8	\N	\N	\N	37
497	BEL	ZAKARIYAE	\N	\N	7511	H	O	\N	\N	2022-09-02	1999-12-05	8	\N	\N	\N	34
498	BEN	CHAIMAE	\N	\N	31100	F	P	\N	\N	2024-09-04	2002-01-01	8	\N	\N	\N	44
500	BRA	ABDERRAZZAK	\N	\N	6307	H	O	\N	\N	2021-11-01	1995-01-06	3	\N	\N	\N	28
502	MAS	TARIK	\N	\N	5905	H	O	\N	\N	2021-07-24	1992-09-10	8	\N	\N	\N	23
505	ECH	RAFIK	\N	\N	32110	H	P	\N	\N	2025-03-10	1997-07-27	1	\N	\N	\N	14
506	BOU	RACHID	\N	\N	30855	H	A	\N	\N	2024-07-17	1999-05-15	8	\N	\N	\N	44
507	MOU	ISSAM	\N	\N	9607	H	A	\N	\N	2023-08-25	2001-01-16	4	\N	\N	\N	1
508	ES-	AMINE	\N	\N	32261	H	P	\N	\N	2025-04-28	1999-06-11	1	\N	\N	\N	22
510	LAH	YOUSSEF	\N	\N	33222	H	O	\N	\N	2025-09-04	2002-06-16	4	\N	\N	\N	38
512	TOU	AISSA	\N	\N	30359	H	A	\N	\N	\N	\N	8	\N	\N	\N	17
513	KAB	HICHAM	\N	\N	2389	H	A	\N	\N	2020-02-17	1986-02-15	8	\N	\N	\N	6
514	ATT	MOHAMMED	\N	\N	31802	H	E	\N	\N	2024-11-14	1999-05-04	7	\N	\N	\N	48
515	HAL	DRISS	\N	\N	5467	H	O	\N	\N	2019-05-02	1996-10-16	8	\N	\N	\N	34
517	BAR	EL MUSTAPHA	\N	\N	33034	H	P	\N	\N	2025-10-06	1988-11-15	8	\N	\N	\N	12
518	EL	MAROUANE	\N	\N	8329	H	O	\N	\N	2022-12-07	1996-12-25	8	\N	\N	\N	34
520	KRI	LOUBNA	\N	\N	30569	F	P	\N	\N	2024-03-14	2002-03-10	8	\N	\N	\N	15
521	El	Soumia	\N	\N	33870	H	P	\N	\N	2025-10-09	2001-12-21	2	\N	\N	\N	52
522	EL-	Youssef	\N	\N	33033	H	A	\N	\N	2025-10-06	1987-11-05	8	\N	\N	\N	17
524	EL	MOUAD	\N	\N	32529	H	A	\N	\N	2025-06-05	2001-06-05	8	\N	\N	\N	30
525	IDB	ABDERRAHMAN	\N	\N	8513	H	O	\N	\N	2020-12-31	1991-08-07	8	\N	\N	\N	23
526	KER	BADR EDDINE	\N	\N	31787	H	O	\N	\N	2024-11-08	2000-11-16	3	\N	\N	\N	28
527	SOU	SALAH EDDINE	\N	\N	33004	H	P	\N	\N	2025-07-31	1993-12-29	8	\N	\N	\N	15
529	CHA	YAHIA	\N	\N	4542	H	P	\N	\N	2021-01-04	1981-03-26	8	\N	\N	\N	15
530	MAR	OTMANE	\N	\N	1656	N	P	\N	\N	2019-10-17	1987-01-10	2	\N	\N	\N	9
532	KHA	ADIL	\N	\N	5242	H	O	\N	\N	2021-05-06	1981-01-26	8	\N	\N	\N	15
534	BOU	ANWAR	\N	\N	31534	H	A	\N	\N	2024-10-17	2000-11-30	8	\N	\N	\N	6
536	EN-	MOHAMED	\N	\N	6055	H	P	\N	\N	2021-09-18	1992-11-29	8	\N	\N	\N	17
540	EL	IMANE	\N	\N	30455	F	P	\N	\N	2024-02-14	2000-05-15	8	\N	\N	\N	31
541	ACH	RACHID	\N	\N	1168	H	P	\N	\N	2019-07-11	1986-07-01	8	\N	\N	\N	23
542	SRO	ISMAIL	\N	\N	SF57833	N	P	\N	\N	2024-09-19	\N	8	\N	\N	\N	43
543	EL	EL MEHDI	\N	\N	7121	H	O	\N	\N	2022-03-04	1995-07-16	8	\N	\N	\N	23
547	BOU	CHAKIB	\N	\N	5471	H	P	\N	\N	2021-05-08	1993-05-04	8	\N	\N	\N	23
549	AIS	ILIAS	\N	\N	31977	H	P	\N	\N	2025-01-20	1997-06-20	8	\N	\N	\N	6
551	MOS	ABDELLATIF	\N	\N	5633	H	P	\N	\N	2021-05-23	1994-06-14	8	\N	\N	\N	23
552	BER	MOHAMED	\N	\N	8194	H	P	\N	\N	2020-11-25	1999-10-08	8	\N	\N	\N	15
555	BOU	HAMZA	\N	\N	5250	H	P	\N	\N	2021-03-20	1996-11-11	8	\N	\N	\N	30
1109	test_employe_1	test_prenom_employe_1	\N	\N	\N	\N	\N	\N	\N	\N	\N	12	\N	ali@test.com	0606060606	\N
1110	test_employe_2	test_prenom_employe_2	123456		12345678	F	E	Analyst	CDD	2021-02-01	1997-05-10	5	\N	sara@test.com		33
3	#REF!	#REF!	\N	\N	2908	H	P	\N	\N	2020-07-27	1972-07-21	8	\N	\N	\N	30
4	RHA	ABDERRAHIM	\N	\N	5885	H	P	\N	\N	2021-08-26	1986-08-04	8	\N	\N	\N	15
5	SAL	Youssef	\N	\N	33043	H	A	\N	\N	2025-09-08	1991-04-11	8	\N	\N	\N	15
6	FAD	AMINE	\N	\N	2678	H	P	\N	\N	2020-06-29	1992-05-01	8	\N	\N	\N	6
7	EZZ	ABDELLATIF	\N	\N	6525	H	O	\N	\N	2021-11-12	1999-07-30	8	\N	\N	\N	6
8	OUL	NABIL	\N	\N	33007	H	E	\N	\N	2025-08-04	2001-06-21	10	\N	\N	\N	10
9	JEB	ELHASSANIA	\N	\N	7183	N	A	\N	\N	2022-03-18	1999-10-29	8	\N	\N	\N	31
10	SAF	ZAKARIA	\N	\N	30832	H	A	\N	\N	2024-07-11	1999-08-26	8	\N	\N	\N	23
12	NAC	BADR-EDDINE	\N	\N	4125	H	P	\N	\N	2020-12-03	1988-12-16	8	\N	\N	\N	30
13	BEN	RACHID	\N	\N	311	H	P	\N	\N	2018-06-18	1979-01-01	8	\N	\N	\N	34
14	EZA	MOHAMED	\N	\N	32893	H	A	\N	\N	2025-07-23	2003-06-07	8	\N	\N	\N	6
558	NKH	ABDERRAFII	\N	\N	461	N	A	\N	\N	2018-12-17	1986-06-16	8	\N	\N	\N	44
562	EL	YOUNESS	\N	\N	699	H	E	\N	\N	2019-04-15	1993-08-06	8	\N	\N	\N	30
564	AYO	ASKARI	\N	\N	7305	H	O	\N	\N	2020-06-25	1997-07-05	8	\N	\N	\N	15
570	OUJ	Ali	\N	\N	31958	H	O	\N	\N	2024-12-29	1999-08-12	4	\N	\N	\N	1
571	KAD	AZZEDINE	\N	\N	30363	H	E	\N	\N	2024-03-11	1989-01-18	2	\N	\N	\N	19
573	BZI	Marwane	\N	\N	33035	H	A	\N	\N	2025-09-01	1993-04-06	8	\N	\N	\N	6
576	AFI	OUSSAMA	\N	\N	32992	H	A	\N	\N	2025-07-31	2001-02-23	8	\N	\N	\N	15
577	KAC	BRAHIM	\N	\N	32373	H	P	\N	\N	2025-06-16	1989-12-02	8	\N	\N	\N	30
579	El	SALIM	\N	\N	31936	H	O	\N	\N	2024-12-27	1998-06-15	3	\N	\N	\N	28
580	EL	KHALID	\N	\N	1218	H	P	\N	\N	2019-07-18	1990-01-28	8	\N	\N	\N	23
581	LEB	OSSAMA	\N	\N	32267	H	P	\N	\N	2025-06-16	1992-03-23	8	\N	\N	\N	30
587	AMI	MOHAMED	\N	\N	6851	H	P	\N	\N	2020-01-02	1994-02-02	8	\N	\N	\N	31
590	DAB	DRISS	\N	\N	8376	H	O	\N	\N	2022-12-16	1993-06-01	3	\N	\N	\N	28
592	EL	Marouane	\N	\N	33459	H	A	\N	\N	2025-10-06	2006-09-01	8	\N	\N	\N	6
593	MAM	ISMAIL	\N	\N	32246	H	P	\N	\N	2025-03-17	1998-02-13	4	\N	\N	\N	4
596	MOH	ELAATAR	\N	\N	3639	H	P	\N	\N	2020-10-19	1987-03-31	2	\N	\N	\N	35
597	EL	SAID	\N	\N	8253	H	P	\N	\N	2022-11-25	1992-05-01	8	\N	\N	\N	34
600	Ben	Hamza	\N	\N	33780	H	O	\N	\N	2025-10-30	2000-11-19	8	\N	\N	\N	23
601	OMA	BRAHIM	\N	\N	8921	H	O	\N	\N	2023-03-22	1998-03-18	8	\N	\N	\N	30
602	OUA	ABDELHAFID	\N	\N	32368	H	P	\N	\N	2025-06-23	2001-04-20	4	\N	\N	\N	4
603	TOU	YOUSSEF	\N	\N	4541	H	P	\N	\N	2021-01-04	1995-01-13	8	\N	\N	\N	15
604	ZAA	RIDA	\N	\N	32271	H	P	\N	\N	2025-05-19	1999-08-26	1	\N	\N	\N	22
605	EL	ZAKARIA	\N	\N	30893	H	E	\N	\N	2024-08-05	1983-12-10	7	\N	\N	\N	20
607	EL	ABDESSAMAD	\N	\N	32525	H	A	\N	\N	2025-06-05	2003-10-23	8	\N	\N	\N	30
610	MEC	OMAR	\N	\N	30869	H	P	\N	\N	2024-07-18	1995-03-05	8	\N	\N	\N	15
612	HAN	AHMED	\N	\N	9472	H	P	\N	\N	2023-08-17	1994-01-15	1	\N	\N	\N	40
613	ZOU	MOHAMED	\N	\N	6642	N	P	\N	\N	2021-11-20	1989-01-01	8	\N	\N	\N	23
614	GUE	MOHAMMED	\N	\N	32278	H	P	\N	\N	2025-05-05	2002-09-08	10	\N	\N	\N	10
615	STO	ANIS	\N	\N	3637	H	P	\N	\N	2020-10-19	1998-09-06	8	\N	\N	\N	30
616	ENN	SAID	\N	\N	30874	H	A	\N	\N	2024-07-29	1992-12-24	1	\N	\N	\N	22
617	ES-	ABDELFATTAH	\N	\N	30681	H	P	\N	\N	2024-05-16	1990-01-03	8	\N	\N	\N	30
619	ED-	HANAE	\N	\N	31199	F	E	\N	\N	2024-09-19	2002-03-02	6	\N	\N	\N	47
620	EL	MOHAMED	\N	\N	207	H	E	\N	\N	2018-03-22	1992-12-25	4	\N	\N	\N	4
621	BEL	SALAHEDDINE	\N	\N	32258	H	P	\N	\N	2025-04-28	1996-01-08	9	\N	\N	\N	49
622	#REF!	BRAHIM	\N	\N	32115	H	#REF!	\N	\N	2025-03-03	2000-04-12	4	\N	\N	\N	53
623	HAO	AYOUB	\N	\N	7830	H	O	\N	\N	2022-10-14	1998-06-06	8	\N	\N	\N	34
624	BIR	Zakaria	\N	\N	33312	H	P	\N	\N	2025-10-06	1998-01-23	8	\N	\N	\N	12
625	NOU	OUSSAMA	\N	\N	32985	H	P	\N	\N	2025-07-31	1999-09-25	8	\N	\N	\N	15
628	AIT	MOUAD	\N	\N	31174	H	E	\N	\N	2024-09-12	2000-09-25	8	\N	\N	\N	30
629	BOU	CHADIA	\N	\N	31101	F	P	\N	\N	2024-09-04	2002-09-15	8	\N	\N	\N	44
634	MAK	YASSINE	\N	\N	805	H	P	\N	\N	2019-05-06	1988-12-06	1	\N	\N	\N	16
635	BTI	ABDELHADI	\N	\N	394	H	P	\N	\N	2018-09-10	1985-01-01	8	\N	\N	\N	34
636	KHO	ANASS	\N	\N	31531	H	A	\N	\N	2024-10-17	1997-04-08	8	\N	\N	\N	6
638	ECH	ABDERRAHMANE	\N	\N	32219	H	A	\N	\N	2025-03-26	1991-07-27	8	\N	\N	\N	44
641	CHA	MOHAMED	\N	\N	30543	H	A	\N	\N	2024-03-14	1996-04-07	8	\N	\N	\N	6
643	EN-	BILAL	\N	\N	31303	H	P	\N	\N	2024-10-03	1995-02-05	8	\N	\N	\N	30
645	SLI	YASSIN	\N	\N	6850	H	O	\N	\N	2022-01-13	1987-09-08	3	\N	\N	\N	28
646	KAO	Amine	\N	\N	33316	H	P	\N	\N	2025-09-22	2000-04-19	4	\N	\N	\N	53
647	OUA	OTHMAN	\N	\N	32268	H	E	\N	\N	2025-04-21	1983-07-01	10	\N	\N	\N	10
650	KHO	LAHCEN	\N	\N	30984	H	P	\N	\N	2024-09-05	1995-05-16	8	\N	\N	\N	30
651	ER-	OMAR	\N	\N	32544	H	O	\N	\N	2025-06-02	1996-09-05	8	\N	\N	\N	34
652	BAT	RIDA	\N	\N	31193	H	P	\N	\N	2024-09-23	1997-06-20	8	\N	\N	\N	6
653	RHA	ABDERRAHMAN	\N	\N	8265	H	A	\N	\N	2022-12-03	1997-05-11	8	\N	\N	\N	6
654	AIS	Abdelali	\N	\N	7286	H	P	\N	\N	\N	\N	4	\N	\N	\N	1
656	HOU	ZINEB	\N	\N	33498	F	P	\N	\N	2025-11-03	1998-10-04	2	\N	\N	\N	27
657	ABI	AYOUB	\N	\N	9677	H	A	\N	\N	2023-09-04	1998-09-12	4	\N	\N	\N	38
658	EL	ABDELILAH	\N	\N	7837	H	O	\N	\N	2022-10-14	1990-08-31	8	\N	\N	\N	34
659	EL-	Mehdi	\N	\N	33310	H	P	\N	\N	2025-10-13	1995-01-07	8	\N	\N	\N	44
664	LAR	MOHAMMED	\N	\N	9073	H	P	\N	\N	2021-05-11	1994-11-13	8	\N	\N	\N	34
665	HAD	ACHRAF	\N	\N	8098	H	O	\N	\N	2022-11-19	1996-04-07	8	\N	\N	\N	30
666	JAB	MOHAMMED	\N	\N	31536	H	A	\N	\N	2024-10-17	2003-03-02	8	\N	\N	\N	6
667	HAR	SOFYAN	\N	\N	31512	H	P	\N	\N	2024-10-12	2003-03-05	8	\N	\N	\N	17
668	GUE	FOUAD	\N	\N	5443	N	A	\N	\N	2021-04-24	1992-04-25	8	\N	\N	\N	15
669	ROU	MOHAMMED	\N	\N	33464	H	E	\N	\N	2025-10-20	1987-12-20	2	\N	\N	\N	19
670	ECH	AYOUB	\N	\N	6865	H	P	\N	\N	2022-01-02	1993-04-07	8	\N	\N	\N	34
672	ZAR	ILYASS	\N	\N	664	H	P	\N	\N	2019-04-08	1987-01-06	4	\N	\N	\N	53
673	ELM	ABDERRAZAK	\N	\N	32795	H	P	\N	\N	2025-07-08	1997-04-12	8	\N	\N	\N	6
675	CHA	AHMED	\N	\N	851	H	P	\N	\N	2019-05-20	1988-02-13	8	\N	\N	\N	23
677	ATO	RACHID	\N	\N	9773	H	O	\N	\N	2023-09-20	1993-09-18	8	\N	\N	\N	23
678	NEH	MUSTAPHA	\N	\N	8295	H	O	\N	\N	2022-12-04	1997-06-15	2	\N	\N	\N	9
679	DER	OTMANE	\N	\N	33490	H	E	\N	\N	2025-10-13	2002-11-20	2	\N	\N	\N	9
680	ARI	ADNANE	\N	\N	7390	H	O	\N	\N	2020-07-29	1998-02-20	8	\N	\N	\N	30
681	AIT	ZAKARIA	\N	\N	30186	H	C	\N	\N	2023-12-11	1994-08-20	8	\N	\N	\N	31
684	NEJ	OUSSAMA	\N	\N	31171	H	E	\N	\N	2024-09-16	1995-08-25	1	\N	\N	\N	22
687	EL	AZIZ	\N	\N	9175	H	A	\N	\N	2023-06-14	1995-09-07	8	\N	\N	\N	34
688	ZHI	AYA	\N	\N	31112	F	A	\N	\N	2024-09-04	2003-09-17	8	\N	\N	\N	44
689	KHR	MORAD	\N	\N	32365	H	P	\N	\N	2025-06-02	1984-01-04	8	\N	\N	\N	30
15	TAT	AZIZ	\N	\N	848	H	E	\N	\N	2019-05-20	1986-02-18	8	\N	\N	\N	30
16	EL	YOUSSEF	\N	\N	156	H	E	\N	\N	2018-02-15	1984-05-23	10	\N	\N	\N	10
17	OBA	ANOUAR	\N	\N	5477	H	A	\N	\N	2021-05-15	1992-08-29	8	\N	\N	\N	15
18	KIR	WIDAD	\N	\N	30652	F	E	\N	\N	2024-04-25	1995-05-24	6	\N	\N	\N	26
19	ALS	MOUAYED	\N	\N	32270	H	E	\N	\N	2025-05-12	1994-10-14	10	\N	\N	\N	10
20	EL	Mohamed	\N	\N	33021	N	A	\N	\N	2025-08-25	2002-11-15	8	\N	\N	\N	44
21	EL	Ismail	\N	\N	32528	H	A	\N	\N	2025-06-05	2000-12-28	8	\N	\N	\N	30
22	GHA	MOUAD	\N	\N	7576	H	P	\N	\N	2022-09-10	2001-06-19	8	\N	\N	\N	30
23	TAN	AMINE	\N	\N	3988	H	P	\N	\N	2020-11-07	1995-05-23	8	\N	\N	\N	17
24	ZAR	RACHID	\N	\N	31748	H	E	\N	\N	2024-11-04	1990-03-28	8	\N	\N	\N	30
25	ESS	Mohamed	\N	\N	31607	H	E	\N	\N	2024-10-28	1991-02-19	8	\N	\N	\N	43
26	EL	BILAL	\N	\N	9308	H	A	\N	\N	2023-07-26	2000-04-16	8	\N	\N	\N	44
27	CHA	ACHRAF	\N	\N	31914	H	O	\N	\N	2024-12-12	1999-08-16	8	\N	\N	\N	30
28	AIT	ABDELHAKIM	\N	\N	2937	H	E	\N	\N	2020-09-03	1993-01-01	8	\N	\N	\N	30
29	SAB	AHMED	\N	\N	7284	H	O	\N	\N	2022-06-26	1997-01-15	8	\N	\N	\N	23
30	MIH	JAWAD	\N	\N	6472	H	E	\N	\N	2021-11-22	1990-05-02	8	\N	\N	\N	30
31	BAR	OMAR	\N	\N	8576	H	O	\N	\N	2023-01-07	1984-12-26	8	\N	\N	\N	15
283	EL	MOHAMED	\N	\N	282	H	A	\N	\N	\N	\N	8	\N	\N	\N	23
690	EL	ANASSE	\N	\N	32765	H	P	\N	\N	2025-07-08	1999-03-20	8	\N	\N	\N	15
693	ABD	ALAMI-CHENTOUFI	\N	\N	33489	H	P	\N	\N	2025-10-13	2001-01-31	2	\N	\N	\N	9
694	EL	OUSSAMA	\N	\N	32274	H	P	\N	\N	2025-05-19	1996-06-26	1	\N	\N	\N	24
699	KHA	SALMA	\N	\N	33904	F	E	\N	\N	2025-10-27	2002-12-21	2	\N	\N	\N	39
700	BAN	SALMA	\N	\N	32952	F	P	\N	\N	2025-07-23	2002-08-11	8	\N	\N	\N	44
703	ALA	ABDELKARIM	\N	\N	8898	H	E	\N	\N	2023-03-30	1995-10-12	8	\N	\N	\N	30
704	ZAO	ABDELGHANI	\N	\N	2787	H	P	\N	\N	2020-07-09	1989-08-03	8	\N	\N	\N	17
707	AMJ	MOHAMED	\N	\N	32370	H	P	\N	\N	2025-06-16	1988-01-08	8	\N	\N	\N	30
708	AMZ	SOUFIANE	\N	\N	32737	H	P	\N	\N	2023-07-31	1999-05-04	8	\N	\N	\N	34
709	EL	YOUNESS	\N	\N	9975	H	P	\N	\N	2023-10-19	1993-05-25	8	\N	\N	\N	30
711	BAH	MOUAD	\N	\N	7506	H	O	\N	\N	2022-09-25	1997-03-29	8	\N	\N	\N	34
715	AMA	ABDESSALAM	\N	\N	31966	H	E	\N	\N	2024-12-19	1995-11-06	8	\N	\N	\N	15
716	HAS	MOHAMED	\N	\N	2363	H	A	\N	\N	2020-03-02	1986-01-01	8	\N	\N	\N	30
717	AMM	ABDELILAH	\N	\N	8336	H	A	\N	\N	2022-12-07	1997-07-18	8	\N	\N	\N	34
718	EL	RACHID	\N	\N	7003	H	P	\N	\N	2022-02-20	1994-05-20	2	\N	\N	\N	9
720	MOH	DEBBAH	\N	\N	5597	H	P	\N	\N	2021-06-10	1991-11-04	2	\N	\N	\N	13
721	AAR	NAJIM	\N	\N	5592	H	P	\N	\N	2021-06-10	1995-10-09	8	\N	\N	\N	30
725	KHO	SAMIR	\N	\N	953	H	A	\N	\N	2019-06-13	1986-01-09	8	\N	\N	\N	30
731	ISM	BENABBAS	\N	\N	33412	H	P	\N	\N	2025-09-01	2003-11-13	8	\N	\N	\N	15
732	SOU	RABIE	\N	\N	33010	H	P	\N	\N	2025-08-04	1991-01-01	8	\N	\N	\N	12
733	AL	ABDELJALIL	\N	\N	8335	H	O	\N	\N	2022-12-07	1996-11-21	8	\N	\N	\N	34
734	BOU	ALAE	\N	\N	32986	H	A	\N	\N	2025-07-31	1987-01-01	8	\N	\N	\N	15
738	ADA	MOHAMMED	\N	\N	6100	H	O	\N	\N	2021-09-04	1990-05-23	8	\N	\N	\N	23
739	SAB	MAROUAN	\N	\N	31362	H	E	\N	\N	2024-10-10	1993-01-22	1	\N	\N	\N	22
744	MLI	AZ-EDDINE	\N	\N	32991	H	P	\N	\N	2025-07-31	2003-04-13	8	\N	\N	\N	15
745	BEN	AYOUB	\N	\N	31920	H	P	\N	\N	\N	\N	4	\N	\N	\N	4
748	BEL	YASSINE	\N	\N	9958	H	A	\N	\N	2023-10-05	2001-04-26	8	\N	\N	\N	44
749	BAL	ABDELHAK	\N	\N	32233	H	E	\N	\N	2025-04-07	1987-02-03	10	\N	\N	\N	10
752	EL	OTMAN	\N	\N	5064	H	P	\N	\N	2021-03-06	1988-02-02	8	\N	\N	\N	30
754	CHB	EL MAHJOUB	\N	\N	452	H	P	\N	\N	2018-12-26	1996-02-05	8	\N	\N	\N	30
755	FAL	Youssef	\N	\N	33200	H	P	\N	\N	2025-08-26	1998-11-25	8	\N	\N	\N	30
756	ZEF	HASSAN	\N	\N	1189	H	A	\N	\N	2019-07-15	1984-12-02	8	\N	\N	\N	6
757	KAM	ABDERRAHIM	\N	\N	9577	H	O	\N	\N	2023-08-20	1993-02-17	8	\N	\N	\N	15
758	MED	MOHAMED	\N	\N	31008	H	A	\N	\N	2024-08-29	1998-02-08	8	\N	\N	\N	43
759	MUS	ELGHAZOUANI	\N	\N	4775	H	P	\N	\N	2021-01-24	1991-03-28	2	\N	\N	\N	13
760	DAG	MOHAMMED	\N	\N	8834	H	O	\N	\N	2023-03-04	1998-01-01	8	\N	\N	\N	23
761	HAR	MOHAMED	\N	\N	30169	H	P	\N	\N	2023-12-11	1998-01-06	8	\N	\N	\N	6
764	ELB	NOUR- EDDINE	\N	\N	8192	H	O	\N	\N	2022-11-25	2000-12-26	8	\N	\N	\N	34
765	AZA	FAHD	\N	\N	9848	H	A	\N	\N	2023-09-26	1999-11-20	8	\N	\N	\N	30
767	EL	ISMAIL	\N	\N	2458	H	P	\N	\N	2020-03-12	1996-04-09	4	\N	\N	\N	1
768	AL	Najoua	\N	\N	33461	F	P	\N	\N	2025-10-06	1993-06-15	8	\N	\N	\N	12
773	ALO	MOHAMMED	\N	\N	31906	H	P	\N	\N	2024-12-05	1987-06-09	4	\N	\N	\N	45
774	#REF!	BADER	\N	\N	9706	H	#REF!	\N	\N	2023-09-05	1997-10-20	4	\N	\N	\N	53
775	GLI	OUSSAMA	\N	\N	31471	H	P	\N	\N	2024-10-08	2002-09-05	8	\N	\N	\N	17
778	EL	ABDELILAH	\N	\N	30484	H	P	\N	\N	2024-03-11	1987-08-25	4	\N	\N	\N	4
779	SAT	AYOUB	\N	\N	6264	H	O	\N	\N	2021-10-11	1997-01-12	4	\N	\N	\N	53
780	ALO	ABDERRAHMAN	\N	\N	31099	H	P	\N	\N	2024-09-04	2003-07-30	8	\N	\N	\N	44
781	ABD	MAHROUSS	\N	\N	5031	H	P	\N	\N	2021-03-18	1993-07-11	2	\N	\N	\N	35
782	RAD	MAHDI	\N	\N	33053	H	P	\N	\N	2025-08-08	2002-08-28	8	\N	\N	\N	30
784	EL	AYOUB	\N	\N	31301	H	P	\N	\N	2024-09-30	1998-04-24	4	\N	\N	\N	38
786	ESS	SOUKAINA	\N	\N	3270	H	P	\N	\N	2020-09-24	1990-08-17	8	\N	\N	\N	23
789	KHA	AYMAN	\N	\N	30656	H	P	\N	\N	\N	\N	4	\N	\N	\N	4
790	NOU	MOUHCINE	\N	\N	3282	H	P	\N	\N	2020-10-01	1993-04-18	8	\N	\N	\N	17
791	HAM	AYMAN	\N	\N	32989	H	A	\N	\N	2025-07-31	2002-07-03	8	\N	\N	\N	15
792	SAR	BOUJIDA	\N	\N	9411	F	P	\N	\N	2023-08-09	1993-10-24	2	\N	\N	\N	39
797	ESS	NASRY	\N	\N	30458	H	P	\N	\N	2022-02-14	1996-03-09	8	\N	\N	\N	34
801	Lah	mohamed	\N	\N	33993	H	A	\N	\N	2025-11-04	2004-06-15	8	\N	\N	\N	12
802	LAG	ISSMAIL	\N	\N	9114	H	O	\N	\N	2023-06-03	1990-02-02	2	\N	\N	\N	27
803	IBN	AHMED	\N	\N	6801	H	O	\N	\N	2021-12-20	1990-05-04	8	\N	\N	\N	34
804	KAO	OUMAIMA	\N	\N	31971	F	E	\N	\N	2025-01-20	2001-03-28	8	\N	\N	\N	30
806	ASS	YOUNESS	\N	\N	31176	H	E	\N	\N	2024-09-19	2001-07-18	8	\N	\N	\N	30
808	KAB	MOHAMED	\N	\N	9396	H	A	\N	\N	2023-08-09	1997-06-03	8	\N	\N	\N	6
809	QOR	AMINE	\N	\N	2380	H	A	\N	\N	2020-02-20	1991-05-09	8	\N	\N	\N	30
812	FRI	LARBI	\N	\N	34029	H	A	\N	\N	2025-10-27	2002-10-27	8	\N	\N	\N	6
696	SAT	ZOUHEIR			5474	F	P			2021-05-08	1989-01-10	8	\N			\N
32	EN-	SOUFIANE	\N	\N	33472	H	P	\N	\N	2025-10-20	1996-06-28	8	\N	\N	\N	12
33	DOU	ANOUAR	\N	\N	32173	H	E	\N	\N	2025-04-07	2001-03-11	7	\N	\N	\N	36
34	ALL	ADNANE	\N	\N	32548	H	E	\N	\N	2025-06-23	1996-09-01	8	\N	\N	\N	43
35	LAM	M'HAMMED	\N	\N	30341	H	A	\N	\N	2024-01-24	2001-07-26	8	\N	\N	\N	17
37	ABD	SABRANE	\N	\N	31002	H	A	\N	\N	2024-08-29	2000-12-27	8	\N	\N	\N	43
38	EL	MOHAMED	\N	\N	7968	H	A	\N	\N	2022-11-02	2000-10-06	8	\N	\N	\N	15
39	ZAK	HAMZA	\N	\N	32670	H	P	\N	\N	2025-07-21	1998-02-24	9	\N	\N	\N	49
40	STI	AIOUB	\N	\N	32264	H	A	\N	\N	2025-05-05	1994-01-01	8	\N	\N	\N	6
41	BEL	IMAD	\N	\N	6117	H	P	\N	\N	2019-09-11	1993-08-04	8	\N	\N	\N	23
42	AIT	MEHDI	\N	\N	33896	H	A	\N	\N	2025-10-23	2004-01-15	2	\N	\N	\N	39
43	ZOU	Mahjoub	\N	\N	33009	H	A	\N	\N	2025-08-04	1991-05-19	8	\N	\N	\N	23
44	MIR	AYOUB	\N	\N	4706	H	E	\N	\N	2021-01-25	1995-01-05	8	\N	\N	\N	30
45	SAI	AARIF	\N	\N	32666	H	P	\N	\N	2025-07-07	1988-01-02	2	\N	\N	\N	27
46	MKI	YOUSSEF	\N	\N	31862	H	P	\N	\N	2024-12-12	1988-01-11	8	\N	\N	\N	6
47	EL	HICHAM	\N	\N	8927	H	P	\N	\N	2023-03-22	1985-05-23	8	\N	\N	\N	23
48	EL	ABDELILAH	\N	\N	30,484	H	P	\N	\N	2024-03-11	1987-08-25	4	\N	\N	\N	4
49	LAR	RAFIK	\N	\N	30810	H	P	\N	\N	2022-06-17	1995-06-20	8	\N	\N	\N	15
50	CHB	OTHMANE	\N	\N	32286	H	P	\N	\N	2025-05-26	1997-06-26	8	\N	\N	\N	43
52	LAC	AYMANE	\N	\N	33115	H	\N	\N	\N	2023-08-22	2002-02-08	8	\N	\N	\N	34
53	EL	JIHAD	\N	\N	31720	H	A	\N	\N	2024-10-31	1994-02-18	8	\N	\N	\N	3
54	KOU	Oussama	\N	\N	33869	H	A	\N	\N	2025-10-09	2004-11-08	8	\N	\N	\N	43
55	EL	JAWAD	\N	\N	31717	H	E	\N	\N	2024-10-31	1990-09-11	1	\N	\N	\N	22
56	BEN	SAAD	\N	\N	32172	H	P	\N	\N	2025-04-21	2000-01-03	8	\N	\N	\N	15
57	BEN	SOUFIANE	\N	\N	7114	H	E	\N	\N	2022-03-03	1995-10-25	8	\N	\N	\N	30
58	ZAR	YOUSSEF	\N	\N	8084	H	O	\N	\N	2022-11-12	1995-01-01	4	\N	\N	\N	53
59	TAH	JAOUAD	\N	\N	6815	H	P	\N	\N	2021-12-31	1990-10-08	8	\N	\N	\N	34
61	MEZ	GHIZLANE	\N	\N	33486	F	P	\N	\N	2025-09-29	1998-02-19	8	\N	\N	\N	12
62	BEN	NOUREDDINE	\N	\N	32890	H	P	\N	\N	2025-07-23	2000-01-04	8	\N	\N	\N	6
813	DAK	YOUNES	\N	\N	8103	H	P	\N	\N	2020-11-19	1995-03-30	8	\N	\N	\N	31
814	BOU	ISSAM	\N	\N	31294	H	E	\N	\N	\N	1993-03-03	4	\N	\N	\N	41
816	LAH	HAMZA	\N	\N	32111	H	E	\N	\N	2025-03-24	2000-01-03	1	\N	\N	\N	40
819	OUZ	LAHCEN	\N	\N	174	H	P	\N	\N	\N	1987-02-25	4	\N	\N	\N	46
820	LAH	MOHAMED	\N	\N	4545	H	P	\N	\N	2021-01-04	1994-05-19	8	\N	\N	\N	15
823	KAR	AMINE	\N	\N	32520	H	A	\N	\N	2025-06-05	1995-10-06	8	\N	\N	\N	30
824	YAC	YOUSSEF	\N	\N	33901	H	E	\N	\N	2025-10-20	1991-08-06	2	\N	\N	\N	27
825	KAS	MOSTAFA	\N	\N	30956	H	A	\N	\N	2024-08-19	2003-09-05	8	\N	\N	\N	6
826	OTM	YOUSSEF	\N	\N	30356	H	A	\N	\N	2024-01-24	1997-02-18	8	\N	\N	\N	17
829	BRA	ABDELMAJID	\N	\N	30711	H	A	\N	\N	2024-05-08	1994-02-25	8	\N	\N	\N	6
831	BEN	OUMAYMA	\N	\N	32571	F	O	\N	\N	2025-06-12	2001-01-01	8	\N	\N	\N	30
835	ELK	FOUAD	\N	\N	30560	H	A	\N	\N	2024-03-14	1995-12-15	8	\N	\N	\N	6
836	LAZ	ADEL	\N	\N	7166	H	O	\N	\N	2022-03-18	1995-01-08	8	\N	\N	\N	34
838	LAK	SOUFIANE	\N	\N	32764	H	P	\N	\N	2025-07-08	2000-04-09	8	\N	\N	\N	15
840	LAH	CHAKIB	\N	\N	5127	H	P	\N	\N	2021-04-05	1998-03-23	8	\N	\N	\N	30
841	MAH	AMINE	\N	\N	9421	H	O	\N	\N	2023-08-09	1996-07-12	8	\N	\N	\N	17
842	AIT	AYOUB	\N	\N	31195	H	P	\N	\N	2024-09-23	1995-05-25	8	\N	\N	\N	43
843	ER-	EL MUSTAPHA	\N	\N	2956	H	E	\N	\N	2020-09-07	1988-11-12	8	\N	\N	\N	34
844	LAM	EL MAHDI	\N	\N	33735	H	O	\N	\N	2025-10-03	2001-02-08	2	\N	\N	\N	19
847	HAH	YASSINE	\N	\N	70	H	P	\N	\N	2017-10-02	1987-03-25	8	\N	\N	\N	30
849	BOU	ABDELMOUNIM	\N	\N	9593	H	A	\N	\N	2023-08-25	2002-12-01	8	\N	\N	\N	30
850	SAG	MOURAD	\N	\N	232	H	A	\N	\N	\N	\N	8	\N	\N	\N	23
851	ESS	JAMAL	\N	\N	7394	H	O	\N	\N	2022-07-29	1987-10-04	8	\N	\N	\N	44
852	EL	SAID	\N	\N	33994	H	A	\N	\N	2025-11-04	2006-01-01	8	\N	\N	\N	12
854	EL	AYMAN	\N	\N	33995	H	A	\N	\N	2025-11-04	2005-08-18	8	\N	\N	\N	12
856	AMI	EL-MZANDI	\N	\N	33485	H	P	\N	\N	2025-10-27	1992-02-03	2	\N	\N	\N	9
860	NAD	SOUFIANE	\N	\N	5251	H	P	\N	\N	2021-03-20	1993-11-12	2	\N	\N	\N	19
861	HOU	YASMINE	\N	\N	33868	H	P	\N	\N	2025-10-09	2005-06-19	2	\N	\N	\N	52
865	OUR	HAMZA	\N	\N	32702	H	E	\N	\N	2025-09-08	1995-11-11	8	\N	\N	\N	30
867	ABA	MOHAMED	\N	\N	33402	H	A	\N	\N	2025-09-01	2003-05-13	8	\N	\N	\N	15
869	EL	RACHID	\N	\N	31825	N	A	\N	\N	2024-11-17	2003-07-01	8	\N	\N	\N	15
873	OUH	YOUSSEF	\N	\N	32148	H	A	\N	\N	2025-03-17	1999-03-06	8	\N	\N	\N	30
878	HAL	MOHAMED	\N	\N	7167	H	O	\N	\N	2022-03-18	1989-01-18	8	\N	\N	\N	34
879	CHE	EL MEHDI	\N	\N	7526	H	O	\N	\N	2022-09-25	1996-03-26	8	\N	\N	\N	34
880	EL	MUSTAPHA	\N	\N	6515	H	O	\N	\N	2021-11-12	1993-02-11	2	\N	\N	\N	19
881	BER	BOUTAINA	\N	\N	33321	F	E	\N	\N	2025-09-08	2001-11-05	2	\N	\N	\N	29
883	ELM	MOHAMED	\N	\N	33601	H	P	\N	\N	2025-09-25	2002-10-11	8	\N	\N	\N	30
884	MAS	MHAMMED	\N	\N	3833	H	P	\N	\N	2020-11-09	1992-03-20	4	\N	\N	\N	53
885	RID	ZEGHARI	\N	\N	7816	H	P	\N	\N	2022-10-14	1999-04-10	2	\N	\N	\N	19
886	CHA	NABIL	\N	\N	32979	H	A	\N	\N	2025-07-31	2003-03-23	8	\N	\N	\N	15
887	MOU	ZAKARIYAE	\N	\N	7249	H	O	\N	\N	2022-06-17	1995-04-17	8	\N	\N	\N	6
888	LAZ	IMAD	\N	\N	6148	H	P	\N	\N	2021-09-19	1990-02-06	2	\N	\N	\N	13
889	GAR	CHAKIR	\N	\N	888	H	O	\N	\N	2019-05-27	1983-02-04	8	\N	\N	\N	6
890	ID	YOUNES	\N	\N	32987	H	A	\N	\N	2025-07-31	2002-06-05	8	\N	\N	\N	15
63	SAF	ASMAA	\N	\N	2900	F	P	\N	\N	2020-07-27	1987-08-31	8	\N	\N	\N	31
64	BIY	FAHD	\N	\N	30412	H	P	\N	\N	2024-02-05	1990-12-02	8	\N	\N	\N	12
65	ERR	ALI	\N	\N	31919	H	C	\N	\N	2024-12-19	1994-03-24	8	\N	\N	\N	30
66	EL	DRISS	\N	\N	32475	H	P	\N	\N	2025-06-02	1998-07-06	10	\N	\N	\N	10
67	ROU	BOUCHAIB	\N	\N	32888	H	A	\N	\N	2025-07-23	1991-03-21	8	\N	\N	\N	6
68	ADL	NABIL	\N	\N	30974	H	O	\N	\N	2024-08-23	2000-04-16	8	\N	\N	\N	44
69	#REF!	SALIM	\N	\N	31747	H	#REF!	\N	\N	2024-11-04	2002-10-17	4	\N	\N	\N	4
70	HAT	Nour-Eddine	\N	\N	33046	H	P	\N	\N	2025-09-08	2000-01-19	2	\N	\N	\N	39
71	KHA	LAHCEN	\N	\N	31014	H	A	\N	\N	2024-08-29	1998-04-10	8	\N	\N	\N	43
73	EL	NORDDINE	\N	\N	552	H	P	\N	\N	2019-02-28	1988-09-26	8	\N	\N	\N	17
74	ZIR	Yahya	\N	\N	33042	H	P	\N	\N	2025-09-08	1995-02-08	10	\N	\N	\N	10
75	AKK	YASSINE	\N	\N	31601	H	A	\N	\N	2024-10-21	2002-11-01	8	\N	\N	\N	30
76	MAH	YASSINE	\N	\N	9584	H	P	\N	\N	2021-08-20	1996-01-29	8	\N	\N	\N	15
77	BOU	MOHAMED	\N	\N	31801	H	A	\N	\N	2024-11-11	1993-02-20	1	\N	\N	\N	22
78	LAA	MOUNIR	\N	\N	31522	H	A	\N	\N	2024-10-15	2000-03-30	8	\N	\N	\N	34
79	MAR	Jaouad	\N	\N	33011	H	P	\N	\N	2025-08-04	1992-07-04	10	\N	\N	\N	10
80	ZAR	HOSSINE	\N	\N	2164	H	P	\N	\N	2019-12-26	1979-02-02	8	\N	\N	\N	44
81	GAR	SAAD	\N	\N	6996	H	E	\N	\N	2022-02-12	1999-11-11	8	\N	\N	\N	30
82	ABI	HASSAN	\N	\N	2831	H	P	\N	\N	2020-07-23	1991-02-01	4	\N	\N	\N	38
83	ZAH	MOUNIA	\N	\N	33898	F	A	\N	\N	2025-10-23	2004-04-28	10	\N	\N	\N	10
84	HA	OTHMANE	\N	\N	30174	N	A	\N	\N	2024-01-29	1996-07-11	4	\N	\N	\N	38
85	RAH	HAMZA	\N	\N	33894	H	A	\N	\N	2025-10-23	2005-08-05	2	\N	\N	\N	39
86	BOU	MAROUANE	\N	\N	7425	H	A	\N	\N	2022-08-03	1998-09-27	8	\N	\N	\N	34
87	FIL	MOHAMMED	\N	\N	30147	H	O	\N	\N	2023-12-01	1991-01-01	8	\N	\N	\N	30
88	BOU	AYMANE	\N	\N	30988	H	P	\N	\N	2024-09-09	2001-04-08	8	\N	\N	\N	30
89	EL	AHMED	\N	\N	7543	H	O	\N	\N	2022-09-02	1996-03-08	8	\N	\N	\N	34
90	#REF!	SMAIL	\N	\N	30839	H	#REF!	\N	\N	2024-07-04	1994-11-08	4	\N	\N	\N	4
91	HMA	MOUAD	\N	\N	30478	H	P	\N	\N	2024-03-04	1992-12-19	4	\N	\N	\N	4
92	BEN	OUSSAMA	\N	\N	31814	H	P	\N	\N	\N	\N	4	\N	\N	\N	4
93	BEN	FAHD	\N	\N	6119	H	P	\N	\N	2019-09-11	1992-12-19	8	\N	\N	\N	23
94	El-	Jamal	\N	\N	32098	H	O	\N	\N	2025-02-21	1998-01-25	8	\N	\N	\N	34
891	KOK	HAMZA	\N	\N	33405	H	P	\N	\N	2025-09-01	2003-07-23	8	\N	\N	\N	15
893	KOL	HOUSSAM	\N	\N	6803	H	O	\N	\N	2021-12-20	1993-03-27	8	\N	\N	\N	34
894	LEM	AYMANE	\N	\N	32519	H	A	\N	\N	2025-06-05	2001-03-07	8	\N	\N	\N	30
896	TAO	ACHRAF	\N	\N	31147	H	A	\N	\N	2024-09-11	2002-02-13	8	\N	\N	\N	44
897	BOU	SAID	\N	\N	9535	H	A	\N	\N	2023-08-22	1994-02-01	4	\N	\N	\N	18
899	SAI	YASSINE	\N	\N	32883	H	A	\N	\N	2025-07-23	2004-01-01	8	\N	\N	\N	15
900	AIT	Abdelilah	\N	\N	33201	H	P	\N	\N	2025-08-26	2000-12-03	8	\N	\N	\N	30
901	GHA	SAAD	\N	\N	32759	H	A	\N	\N	2025-07-01	1996-11-09	8	\N	\N	\N	34
902	MAN	OUSSAMA	\N	\N	32244	H	O	\N	\N	2025-03-31	2002-12-30	8	\N	\N	\N	17
903	BAL	Ismail	\N	\N	33468	H	P	\N	\N	2025-10-20	1996-06-26	8	\N	\N	\N	12
906	MAA	ABDENACEUR	\N	\N	451	H	P	\N	\N	2018-12-26	1994-07-28	8	\N	\N	\N	12
908	EL	MOUAD	\N	\N	6278	H	A	\N	\N	2021-10-18	1996-11-10	8	\N	\N	\N	15
911	BOU	Naima	\N	\N	9998	F	P	\N	\N	2023-10-02	1972-06-04	8	\N	\N	\N	30
912	BEN	MOHAMMED	\N	\N	30622	H	P	\N	\N	2024-03-25	2002-01-13	8	\N	\N	\N	34
913	DRI	MOHAMMED	\N	\N	6698	H	P	\N	\N	2019-12-04	1994-04-22	8	\N	\N	\N	23
914	BOU	MOUHCINE	\N	\N	32780	H	A	\N	\N	2025-07-08	2002-02-01	8	\N	\N	\N	15
916	HIC	ETTI	\N	\N	6270	H	P	\N	\N	2021-10-11	1999-10-25	2	\N	\N	\N	9
917	TRI	ZAIDANE	\N	\N	7635	H	O	\N	\N	2022-09-02	1995-06-29	8	\N	\N	\N	34
919	EL	SOUFIANE	\N	\N	7232	H	O	\N	\N	2020-05-11	1996-11-09	8	\N	\N	\N	15
921	OUK	ISMAIL	\N	\N	6852	H	O	\N	\N	2022-01-02	1990-08-25	8	\N	\N	\N	34
922	EL	KHALID	\N	\N	32285	H	E	\N	\N	\N	1995-10-29	4	\N	\N	\N	53
928	EL	EL HASSAN	\N	\N	5051	H	P	\N	\N	2021-04-05	1988-10-10	8	\N	\N	\N	15
929	EL	RACHID	\N	\N	7555	H	O	\N	\N	2022-09-09	1997-03-01	8	\N	\N	\N	43
930	ELO	ABDELHAQ	\N	\N	8252	H	O	\N	\N	2022-11-27	1991-03-18	8	\N	\N	\N	6
931	ALH	IHSSAN	\N	\N	32266	H	C	\N	\N	2025-06-16	1999-08-24	8	\N	\N	\N	32
932	EL	EL HOUSSAINE	\N	\N	7538	H	O	\N	\N	2022-09-02	1993-04-10	8	\N	\N	\N	34
934	EL	MOUHCINE	\N	\N	33000	H	A	\N	\N	2025-07-31	1994-04-06	8	\N	\N	\N	15
935	TOU	AYMANE	\N	\N	31839	H	A	\N	\N	2024-11-22	2003-09-29	8	\N	\N	\N	34
936	MOH	YOUSSEF	\N	\N	32476	H	E	\N	\N	2025-06-02	1994-03-28	2	\N	\N	\N	39
938	ZER	MOHAMED	\N	\N	31907	H	E	\N	\N	2024-12-09	1985-08-07	8	\N	\N	\N	15
939	MOH	MOUSSA	\N	\N	33603	H	P	\N	\N	2025-09-25	2003-04-10	8	\N	\N	\N	30
941	ADE	AKRAM	\N	\N	32524	H	P	\N	\N	2025-06-05	2004-12-09	8	\N	\N	\N	30
942	EN-	ABDELFATTAH	\N	\N	9748	H	P	\N	\N	2023-10-16	1989-04-12	4	\N	\N	\N	46
945	EL	ANAS	\N	\N	9823	N	O	\N	\N	2023-09-21	\N	8	\N	\N	\N	30
948	AZI	BADR	\N	\N	7504	H	O	\N	\N	2022-09-02	1991-12-31	8	\N	\N	\N	34
949	SEN	OUSAMA	\N	\N	32369	H	P	\N	\N	2025-06-16	1998-03-21	8	\N	\N	\N	12
952	AOU	YASSIN	\N	\N	9055	H	P	\N	\N	2023-05-18	1992-01-01	8	\N	\N	\N	30
95	MOU	ABDELKARIM	\N	\N	8374	H	P	\N	\N	2022-12-16	1998-03-25	8	\N	\N	\N	30
96	ABB	ABDERRAHIM	\N	\N	7235	H	A	\N	\N	2022-05-11	1996-07-28	8	\N	\N	\N	34
97	JER	OUSSAMA	\N	\N	7128	H	P	\N	\N	2022-03-04	1994-07-19	8	\N	\N	\N	30
98	NOU	MOHAMMED	\N	\N	4702	H	P	\N	\N	2021-01-21	1993-11-19	8	\N	\N	\N	17
99	EL	ANAS	\N	\N	33895	H	A	\N	\N	2025-10-23	2006-02-23	1	\N	\N	\N	2
100	EL-	BOULAHIA	\N	\N	8086	H	P	\N	\N	2020-11-12	2000-01-13	8	\N	\N	\N	31
101	QAN	REDA	\N	\N	33097	H	A	\N	\N	2023-08-18	1996-06-17	8	\N	\N	\N	15
102	CHA	RADOUANE	\N	\N	33040	H	P	\N	\N	2025-09-08	1991-05-10	2	\N	\N	\N	27
103	BOU	TAOUFIK	\N	\N	30386	H	A	\N	\N	2024-01-26	2003-06-12	8	\N	\N	\N	17
104	MAM	ISMAIL	\N	\N	9708	H	A	\N	\N	2025-03-17	1998-02-13	4	\N	\N	\N	4
105	YZO	BADR	\N	\N	30579	H	A	\N	\N	2024-03-14	2002-12-28	8	\N	\N	\N	15
107	DEL	EL HOUSSAINE	\N	\N	8001	H	P	\N	\N	2022-11-04	1998-04-27	8	\N	\N	\N	12
108	SLI	AYOUB	\N	\N	30737	H	E	\N	\N	2024-05-20	1993-01-06	10	\N	\N	\N	10
109	ALI	BRAHIM	\N	\N	31600	H	P	\N	\N	2024-10-21	1993-05-25	8	\N	\N	\N	23
110	MAR	ANAS	\N	\N	33471	H	E	\N	\N	2025-10-20	1994-01-01	8	\N	\N	\N	12
111	GOU	AHMED	\N	\N	7298	H	P	\N	\N	2020-06-25	1995-01-01	8	\N	\N	\N	34
112	MOH	SOUFIANE	\N	\N	32690	H	A	\N	\N	2025-06-30	1988-10-05	1	\N	\N	\N	40
113	EZZ	ACHRAF	\N	\N	32970	H	P	\N	\N	2025-07-31	2003-07-22	8	\N	\N	\N	44
114	EL	AYOUB	\N	\N	4621	H	A	\N	\N	2021-01-14	2000-01-01	8	\N	\N	\N	30
115	EL	MOUNSIF	\N	\N	32149	H	P	\N	\N	2025-03-17	2002-01-04	4	\N	\N	\N	53
117	EDD	ISSAM	\N	\N	7737	H	E	\N	\N	2022-10-06	1986-12-14	8	\N	\N	\N	30
118	BAD	BADR	\N	\N	9809	H	O	\N	\N	2023-10-11	1997-12-29	4	\N	\N	\N	25
119	EL-	Kaoutar	\N	\N	31198	F	C	\N	\N	2024-09-23	1995-10-30	6	\N	\N	\N	5
120	BER	SAID	\N	\N	333	H	P	\N	\N	2018-07-02	1985-08-04	8	\N	\N	\N	6
121	MOU	ISSAM	\N	\N	33148	H	O	\N	\N	2025-08-25	2001-01-16	8	\N	\N	\N	44
122	HOU	MOHAMMED	\N	\N	7295	H	O	\N	\N	2022-06-25	1996-05-03	8	\N	\N	\N	34
124	AMA	AYOUB	\N	\N	8349	H	P	\N	\N	2020-12-10	1992-03-28	8	\N	\N	\N	37
125	SAB	RACHID	\N	\N	32367	H	P	\N	\N	2025-06-16	1990-09-02	8	\N	\N	\N	43
126	SAB	NAJIB	\N	\N	32902	H	A	\N	\N	2025-07-23	2000-11-03	8	\N	\N	\N	6
127	ECH	ABDERRAHIM	\N	\N	31184	H	P	\N	\N	2024-09-26	1988-01-31	8	\N	\N	\N	6
128	EL	MOHAMMED	\N	\N	2950	H	P	\N	\N	2020-08-31	1987-03-10	2	\N	\N	\N	35
129	Ram	jawad	\N	\N	7131	H	P	\N	\N	\N	1994-01-01	4	\N	\N	\N	38
130	ZAM	REDOUANE	\N	\N	32252	H	P	\N	\N	2025-04-14	1995-07-12	4	\N	\N	\N	53
132	CHE	KARIM	\N	\N	32784	H	P	\N	\N	2025-07-08	2004-09-20	8	\N	\N	\N	15
133	ABI	AYOUB	\N	\N	33221	H	O	\N	\N	2025-09-04	1998-09-12	4	\N	\N	\N	38
134	EL	YOUSSEF	\N	\N	31186	H	P	\N	\N	2024-09-26	2000-07-08	4	\N	\N	\N	38
136	ESS	ADIL	\N	\N	446	H	P	\N	\N	2018-11-26	1983-04-15	8	\N	\N	\N	6
137	SAI	MOHAMED	\N	\N	33416	H	A	\N	\N	2025-09-01	2003-08-15	8	\N	\N	\N	15
138	LEK	SOUMIA	\N	\N	9712	F	O	\N	\N	2023-09-10	1991-08-18	8	\N	\N	\N	31
139	LAZ	AHMED	\N	\N	30172	H	P	\N	\N	2023-12-25	2000-07-23	8	\N	\N	\N	15
140	CHO	ADNANE	\N	\N	8327	H	O	\N	\N	2022-12-07	2001-01-04	8	\N	\N	\N	34
141	OUM	NABIL	\N	\N	6848	H	P	\N	\N	2022-01-13	1981-12-12	8	\N	\N	\N	30
142	KUI	ECH-CHARIF	\N	\N	7146	H	P	\N	\N	2022-03-11	1994-02-21	8	\N	\N	\N	30
143	TOU	AMINE	\N	\N	32259	H	P	\N	\N	2025-04-28	1998-02-16	8	\N	\N	\N	43
144	BOU	OTHMANE	\N	\N	5270	H	P	\N	\N	2021-04-10	1995-06-06	8	\N	\N	\N	30
953	DAI	SAIF EDDINE	\N	\N	32542	H	O	\N	\N	2025-06-02	1996-04-22	8	\N	\N	\N	34
954	CHI	MAROUAN	\N	\N	32785	N	A	\N	\N	2025-07-08	2004-04-25	8	\N	\N	\N	15
955	IMA	NAJAH	\N	\N	5456	H	P	\N	\N	2021-05-02	1991-04-10	2	\N	\N	\N	35
957	OUA	SOUFIANE	\N	\N	9465	H	P	\N	\N	2023-08-17	1996-12-11	4	\N	\N	\N	53
962	SIA	ANOUAR	\N	\N	34024	H	P	\N	\N	2025-10-27	2001-02-02	8	\N	\N	\N	6
964	MAR	MORAD	\N	\N	7170	H	P	\N	\N	2022-03-18	1993-10-13	8	\N	\N	\N	17
965	ZEL	YOUSSEF	\N	\N	31884	H	O	\N	\N	2024-12-02	2001-09-13	8	\N	\N	\N	3
966	ABO	ISAM	\N	\N	8188	H	O	\N	\N	2022-11-25	1996-05-25	8	\N	\N	\N	15
968	EL	SAID	\N	\N	32708	H	O	\N	\N	2025-07-13	1995-07-06	8	\N	\N	\N	34
1155	test_employe_2	test_prenom_employe_2	123456		12345678a	F	E	Analyst	CDD	2021-02-01	1997-05-10	4	\N	sara@test.com		18
145	NAZ	SAMIR	\N	\N	8979	H	E	\N	\N	2023-04-10	1992-09-14	8	\N	\N	\N	30
146	OUA	LOUBNA	\N	\N	7156	F	O	\N	\N	2022-03-18	1999-07-06	8	\N	\N	\N	17
147	JER	ABDELJALIL	\N	\N	6704	H	O	\N	\N	2021-12-09	1989-01-11	8	\N	\N	\N	34
149	JOU	ANOUAR	\N	\N	1217	H	P	\N	\N	2019-07-18	1982-11-18	8	\N	\N	\N	17
150	ELM	AYOUB	\N	\N	5466	H	P	\N	\N	2021-05-02	1998-12-30	2	\N	\N	\N	27
151	BEN	OMAR	\N	\N	31173	H	P	\N	\N	2024-09-19	2000-01-19	4	\N	\N	\N	41
152	ECH	AMINE	\N	\N	32698	H	P	\N	\N	2025-07-21	1995-12-13	8	\N	\N	\N	43
153	ESS	AICHA	\N	\N	5457	H	P	\N	\N	2021-05-02	1995-12-29	8	\N	\N	\N	23
154	EL	DRISS	\N	\N	34017	H	A	\N	\N	2025-11-06	2004-05-11	8	\N	\N	\N	43
155	YAH	AMINE	\N	\N	31194	H	A	\N	\N	2024-09-23	1990-05-03	8	\N	\N	\N	30
156	EL	HAJAR	\N	\N	6774	F	P	\N	\N	2021-12-18	1997-05-28	8	\N	\N	\N	44
157	DAO	FATIMA EZZAHRA	\N	\N	33479	F	P	\N	\N	2025-09-29	2002-02-27	8	\N	\N	\N	30
158	BAO	TAOUFIK	\N	\N	30977	H	P	\N	\N	2024-08-26	1991-01-17	8	\N	\N	\N	17
159	ZNA	MOHAMED	\N	\N	30463	H	O	\N	\N	2024-02-14	1998-04-11	4	\N	\N	\N	53
161	EZ-	TIJANI	\N	\N	30074	H	P	\N	\N	2023-11-02	1996-07-31	8	\N	\N	\N	30
162	EL	ABAIRISS	\N	\N	32059	H	P	\N	\N	\N	\N	4	\N	\N	\N	4
163	RHA	OUSSAMA	\N	\N	31974	H	P	\N	\N	2025-01-20	2001-05-30	8	\N	\N	\N	30
164	FEL	KHALID	\N	\N	9466	H	P	\N	\N	2023-08-15	1998-12-24	4	\N	\N	\N	53
166	LAH	Abderramane	\N	\N	33008	H	P	\N	\N	2025-08-04	1996-11-07	8	\N	\N	\N	44
167	#REF!	ABDELLATIF	\N	\N	31815	H	#REF!	\N	\N	2024-11-21	1992-06-18	4	\N	\N	\N	4
168	MOU	Zakaria	\N	\N	33467	H	P	\N	\N	2025-10-20	1993-12-20	2	\N	\N	\N	39
169	ALL	Imrane	\N	\N	33317	N	A	\N	\N	2025-10-13	1994-12-23	8	\N	\N	\N	15
170	BOU	MOHAMMED AMINE	\N	\N	7518	H	O	\N	\N	2022-09-09	2000-07-28	8	\N	\N	\N	30
171	EL	FARID	\N	\N	30350	H	A	\N	\N	2024-01-24	2002-12-15	8	\N	\N	\N	15
172	BOU	NABIL	\N	\N	653	H	E	\N	\N	2019-04-01	1985-05-24	1	\N	\N	\N	40
173	RIA	OMAR	\N	\N	31902	H	E	\N	\N	2025-01-16	1998-02-13	8	\N	\N	\N	17
174	BAC	ANAS	\N	\N	32254	H	P	\N	\N	2025-04-14	2000-09-16	1	\N	\N	\N	16
175	ZDA	Ayoub	\N	\N	33019	H	P	\N	\N	2025-08-25	1996-03-19	8	\N	\N	\N	23
176	LAH	BRAHIM	\N	\N	5884	H	P	\N	\N	2021-08-26	1988-04-14	8	\N	\N	\N	44
177	YZO	BADR	\N	\N	30,579	H	A	\N	\N	2024-03-14	2002-12-28	4	\N	\N	\N	38
179	ELA	MEHDI	\N	\N	30675	H	E	\N	\N	2024-06-24	1990-09-25	7	\N	\N	\N	8
181	EL-	AYOUB	\N	\N	31104	H	A	\N	\N	2024-09-04	2001-01-19	8	\N	\N	\N	15
182	MAN	NOUREDDINE	\N	\N	30501	H	P	\N	\N	2022-03-04	1998-06-06	8	\N	\N	\N	23
183	RHA	RACHID	\N	\N	30482	H	P	\N	\N	2024-03-21	1985-12-15	4	\N	\N	\N	46
184	MOR	HAMZA	\N	\N	5065	H	P	\N	\N	2021-03-06	1993-08-01	8	\N	\N	\N	6
971	EL	MOUHCINE	\N	\N	4772	H	P	\N	\N	2021-01-24	1990-08-28	8	\N	\N	\N	34
972	EDD	MOHAMED	\N	\N	31749	H	E	\N	\N	2024-11-07	1989-04-14	1	\N	\N	\N	22
974	BEN	EL ARBI	\N	\N	33311	H	P	\N	\N	2025-10-06	1992-11-01	8	\N	\N	\N	12
975	TAI	ABDELAZIZ	\N	\N	7911	H	O	\N	\N	2022-10-23	1997-08-13	3	\N	\N	\N	28
976	NAF	MEHDI	\N	\N	30921	H	P	\N	\N	2024-08-15	2001-05-03	8	\N	\N	\N	43
979	ELB	MOHAMED	\N	\N	30665	H	E	\N	\N	2024-04-01	1990-06-18	2	\N	\N	\N	39
983	EL	AMINE	\N	\N	31904	H	E	\N	\N	2025-01-16	1998-03-21	8	\N	\N	\N	30
984	BEN	MOHAMMED	\N	\N	33491	H	P	\N	\N	2025-10-16	1998-02-09	8	\N	\N	\N	12
986	IKE	HAMID	\N	\N	6516	H	P	\N	\N	2021-11-12	1995-06-20	8	\N	\N	\N	34
988	MAZ	ISSAM	\N	\N	5079	H	P	\N	\N	2021-03-06	1999-01-02	8	\N	\N	\N	6
989	EL	AHMED	\N	\N	336	H	P	\N	\N	2018-07-04	1989-06-15	8	\N	\N	\N	34
990	EL	YOUSSEF	\N	\N	6734	H	E	\N	\N	2021-12-11	1998-10-10	8	\N	\N	\N	44
992	AYO	LAHMAR	\N	\N	31834	H	A	\N	\N	2024-11-17	2001-01-01	8	\N	\N	\N	15
995	TAN	Saad	\N	\N	33022	H	P	\N	\N	2025-08-25	1999-10-27	4	\N	\N	\N	38
996	BAO	MOHAMED	\N	\N	5272	H	O	\N	\N	2021-04-10	1996-08-20	8	\N	\N	\N	15
997	LAK	YASSER	\N	\N	32789	H	P	\N	\N	2025-07-08	2007-01-19	8	\N	\N	\N	15
999	NIN	AICHA	\N	\N	31332	H	O	\N	\N	2024-09-29	2002-11-22	8	\N	\N	\N	43
1001	SEL	ANAS	\N	\N	32984	H	P	\N	\N	2025-07-31	2000-03-24	8	\N	\N	\N	15
1002	MOU	LAKHRIBI	\N	\N	7592	H	P	\N	\N	2022-09-10	1996-12-01	2	\N	\N	\N	27
1003	#REF!	ABDELMALEK	\N	\N	32155	H	#REF!	\N	\N	2025-03-20	2002-01-19	4	\N	\N	\N	4
1007	EL-	YOUSSEF	\N	\N	31188	H	P	\N	\N	2024-09-23	1996-08-29	4	\N	\N	\N	38
1008	BER	YOUSSEF	\N	\N	8326	H	P	\N	\N	2022-12-07	1997-11-09	8	\N	\N	\N	34
1009	AMO	MOHAMED	\N	\N	7500	H	P	\N	\N	2022-09-02	1994-02-14	8	\N	\N	\N	44
1016	RHO	WALID	\N	\N	259	H	P	\N	\N	2018-03-19	1991-02-11	8	\N	\N	\N	30
1019	ARO	SOUKAINA	\N	\N	31909	F	P	\N	\N	2025-01-16	2001-03-29	8	\N	\N	\N	43
1021	MAI	ANAS	\N	\N	33036	H	P	\N	\N	2025-09-01	2001-03-11	8	\N	\N	\N	12
1022	ELK	ZAKARIA	\N	\N	7565	H	O	\N	\N	2022-09-21	1997-08-19	8	\N	\N	\N	6
1024	ZOU	ABDELILLAH	\N	\N	6871	H	O	\N	\N	2022-01-02	1991-04-05	8	\N	\N	\N	31
1025	OUB	Brahim	\N	\N	30746	H	A	\N	\N	2024-05-13	1983-09-01	1	\N	\N	\N	22
185	EL	ABDELALI	\N	\N	30189	H	P	\N	\N	2023-12-18	1999-03-05	4	\N	\N	\N	25
186	CHA	HAMZA	\N	\N	31978	H	A	\N	\N	2025-01-23	1995-07-12	1	\N	\N	\N	40
187	BOU	OTHMANE	\N	\N	32363	H	P	\N	\N	2025-05-26	1995-01-02	8	\N	\N	\N	15
190	EL	MOHAMED	\N	\N	30258	H	C	\N	\N	2024-01-02	1981-07-26	8	\N	\N	\N	30
191	OUY	SOUHAYLA	\N	\N	7807	H	A	\N	\N	2022-10-12	1999-06-04	8	\N	\N	\N	6
192	EL	MOUNIR	\N	\N	31292	H	P	\N	\N	2024-09-30	1990-01-02	5	\N	\N	\N	42
193	EL	JAOUAD	\N	\N	31530	H	A	\N	\N	2024-10-17	2000-01-28	8	\N	\N	\N	6
194	YZO	MOHAMMED	\N	\N	7725	N	A	\N	\N	2022-10-02	1994-07-21	4	\N	\N	\N	11
195	JAM	SAMIR	\N	\N	32541	H	O	\N	\N	2025-06-02	1991-09-19	8	\N	\N	\N	34
196	BAK	ABDELLAH	\N	\N	34016	H	A	\N	\N	2025-11-06	2005-04-23	8	\N	\N	\N	43
197	Ben	Hamza	\N	\N	30037	H	A	\N	\N	\N	2000-11-19	4	\N	\N	\N	53
198	BEL	MOHAMMED	\N	\N	9045	H	P	\N	\N	2023-04-13	2001-08-02	2	\N	\N	\N	39
199	EL	MOHAMED AMINE	\N	\N	6530	H	P	\N	\N	2021-11-12	1997-04-20	8	\N	\N	\N	34
200	AIS	AYOUB	\N	\N	7287	H	P	\N	\N	2022-06-25	1998-03-27	4	\N	\N	\N	1
202	ES-	ZAKARIA	\N	\N	31196	H	P	\N	\N	2024-10-10	1997-10-03	4	\N	\N	\N	4
203	BIT	ADILE	\N	\N	471	H	C	\N	\N	2019-01-03	1988-11-26	6	\N	\N	\N	7
204	AMA	MOSTAFA	\N	\N	6058	H	P	\N	\N	2021-09-04	1992-08-10	8	\N	\N	\N	17
205	SEK	ELHASANE	\N	\N	9081	H	O	\N	\N	2023-05-11	1999-06-12	8	\N	\N	\N	6
207	BAR	OSSAMA	\N	\N	30190	H	E	\N	\N	2023-12-21	1992-01-10	8	\N	\N	\N	15
208	TAQ	AMINE	\N	\N	30453	H	P	\N	\N	2022-02-14	1999-11-08	8	\N	\N	\N	34
209	ANN	CHARAFEDDINE	\N	\N	31863	H	E	\N	\N	2025-01-16	1976-07-09	8	\N	\N	\N	30
210	RAI	MOHAMMED REDA	\N	\N	4703	H	P	\N	\N	2021-01-21	1993-12-06	8	\N	\N	\N	30
211	ET-	MOHAMED	\N	\N	32109	H	P	\N	\N	2025-03-17	2000-12-19	8	\N	\N	\N	17
212	OUA	ABDELALI	\N	\N	30171	H	P	\N	\N	2023-12-25	1994-03-27	4	\N	\N	\N	38
213	REY	ISSAM	\N	\N	32260	H	P	\N	\N	2025-04-28	1994-04-10	1	\N	\N	\N	22
215	ERR	ABDELHAMID	\N	\N	1527	H	A	\N	\N	2019-09-16	1992-04-05	8	\N	\N	\N	23
216	SAB	ABDELHADI	\N	\N	32696	H	P	\N	\N	2025-07-21	1996-01-03	8	\N	\N	\N	43
217	MOH	MOUNAIM	\N	\N	30306	H	P	\N	\N	2024-02-19	1995-08-09	2	\N	\N	\N	52
219	MHA	ISMAIL	\N	\N	30981	H	E	\N	\N	2024-09-02	1997-06-08	4	\N	\N	\N	4
220	EL	MILOUD	\N	\N	5771	H	O	\N	\N	2021-07-12	1994-01-23	2	\N	\N	\N	9
221	kab	ibtissam	\N	\N	30817	H	A	\N	\N	2024-06-17	1991-04-12	8	\N	\N	\N	15
222	NAH	Saad	\N	\N	33024	H	A	\N	\N	2025-09-01	1995-11-05	8	\N	\N	\N	6
224	ES-	Hamza	\N	\N	33028	H	P	\N	\N	2025-09-01	1992-06-11	8	\N	\N	\N	43
225	ZEM	DRISS	\N	\N	8330	H	O	\N	\N	2022-12-07	1994-09-02	8	\N	\N	\N	34
226	BOU	YASSINE	\N	\N	5722	H	P	\N	\N	2021-06-28	1993-07-20	8	\N	\N	\N	30
227	SAH	HATIM	\N	\N	31846	H	O	\N	\N	2024-11-22	2001-10-12	8	\N	\N	\N	34
228	ASS	MOHAMMED	\N	\N	9442	H	A	\N	\N	2023-08-10	1997-06-19	8	\N	\N	\N	23
229	SGH	SAID	\N	\N	7165	H	O	\N	\N	2022-03-18	1992-11-24	8	\N	\N	\N	34
230	LAN	ZAKARIA	\N	\N	30075	H	O	\N	\N	2023-11-02	1996-12-26	3	\N	\N	\N	28
231	EL	ALAE-EDDINE	\N	\N	31891	H	O	\N	\N	2024-12-02	2000-09-05	8	\N	\N	\N	44
232	EL	MOHAMMED	\N	\N	9089	H	O	\N	\N	2023-05-11	1992-11-11	8	\N	\N	\N	34
233	OUB	OUSSAMA	\N	\N	7608	H	P	\N	\N	2022-09-23	1999-08-07	8	\N	\N	\N	15
235	DAH	OUALID	\N	\N	31190	H	E	\N	\N	2024-09-23	1992-10-21	1	\N	\N	\N	2
236	DIB	AHMED	\N	\N	31856	H	O	\N	\N	2024-11-22	1999-11-24	8	\N	\N	\N	30
1031	NAS	AYMAN	\N	\N	31177	H	P	\N	\N	2024-09-19	2001-02-14	8	\N	\N	\N	30
1032	EZZ	MOHAMMED	\N	\N	7311	H	O	\N	\N	2022-06-25	1994-11-28	8	\N	\N	\N	34
1034	MOU	ABDELOUAJED	\N	\N	6519	H	O	\N	\N	2021-11-12	2000-10-20	8	\N	\N	\N	34
1039	BAH	OTHMANE	\N	\N	30284	H	A	\N	\N	2024-01-10	1998-08-11	4	\N	\N	\N	1
1040	AIM	BENDADA	\N	\N	32995	H	P	\N	\N	2025-07-31	2003-01-03	8	\N	\N	\N	15
1041	ABE	SOULAIMANE	\N	\N	7161	H	O	\N	\N	2022-03-18	1997-06-14	8	\N	\N	\N	34
1042	HAI	ABDENNOUR	\N	\N	30628	H	A	\N	\N	2024-03-25	2001-12-06	8	\N	\N	\N	34
1043	LAC	Walid	\N	\N	33458	H	A	\N	\N	2025-10-06	2005-05-12	8	\N	\N	\N	6
1045	CHA	OTHMANE	\N	\N	30841	H	P	\N	\N	2024-07-04	2001-11-26	8	\N	\N	\N	23
1048	EL	ISSAM	\N	\N	7549	H	P	\N	\N	2022-09-10	1996-12-21	8	\N	\N	\N	30
1147	test_employe_1	test_prenom_employe_1				H				\N	\N	10	\N	ali@test.com	0606060606	10
1158	qqq	qqq	123		68192	H	d	cadre	cdd	2026-02-20	2026-02-11	2	\N	test_email3@s3m.com	0699296620	13
237	EL-	MOHAMMED	\N	\N	32177	H	P	\N	\N	2025-05-19	1996-11-06	8	\N	\N	\N	12
239	FIL	MAROUANE	\N	\N	32262	H	A	\N	\N	2025-04-28	1984-04-18	4	\N	\N	\N	41
240	TAH	MOHAMMED	\N	\N	33899	H	A	\N	\N	2025-10-23	2000-06-30	10	\N	\N	\N	10
241	BOU	HAKIM	\N	\N	31843	H	A	\N	\N	2024-11-22	2001-08-02	8	\N	\N	\N	34
242	LAM	MOHAMED	\N	\N	7147	H	O	\N	\N	2022-03-11	1995-01-05	8	\N	\N	\N	30
243	HAM	Issam	\N	\N	8866	H	P	\N	\N	2023-03-04	1992-12-20	4	\N	\N	\N	46
245	BOU	Benacher	\N	\N	31775	H	O	\N	\N	2024-11-01	2000-02-28	8	\N	\N	\N	15
247	EL	BOUAZZA	\N	\N	30196	H	P	\N	\N	2023-12-13	1999-01-01	2	\N	\N	\N	13
248	QUE	AYOUB	\N	\N	33473	H	E	\N	\N	2025-10-20	1995-05-02	8	\N	\N	\N	12
249	RIN	YOUNESS	\N	\N	32364	H	P	\N	\N	2025-05-26	1998-02-06	10	\N	\N	\N	10
250	SBI	HICHAM	\N	\N	30583	H	E	\N	\N	\N	1993-09-05	4	\N	\N	\N	53
251	MAK	Hamid	\N	\N	33023	H	P	\N	\N	2025-08-25	2001-04-10	4	\N	\N	\N	1
253	HAD	BRAHIM	\N	\N	30680	H	P	\N	\N	2024-05-13	1992-05-19	4	\N	\N	\N	25
254	OUA	AMINE	\N	\N	5274	H	P	\N	\N	2021-04-10	1998-05-30	8	\N	\N	\N	30
255	BAH	YOUNESS	\N	\N	30831	H	P	\N	\N	2024-07-08	1999-05-11	8	\N	\N	\N	23
256	BOU	MOHAMED	\N	\N	32773	H	A	\N	\N	2025-07-08	2001-01-25	8	\N	\N	\N	15
258	SAB	AYMANE	\N	\N	32699	H	P	\N	\N	2025-08-04	1996-02-02	8	\N	\N	\N	12
260	EDD	MOHAMED	\N	\N	30387	H	A	\N	\N	2024-01-26	1999-12-07	8	\N	\N	\N	17
262	DAH	Mohamed	\N	\N	33460	H	P	\N	\N	2025-10-06	1995-12-10	8	\N	\N	\N	30
263	AGO	MOHAMED	\N	\N	32253	H	P	\N	\N	2025-04-14	1988-09-19	4	\N	\N	\N	4
264	CHE	NABIL	\N	\N	31816	H	P	\N	\N	2024-11-21	1994-12-22	4	\N	\N	\N	46
265	GAR	HASSAN	\N	\N	31707	H	O	\N	\N	2024-10-27	1998-04-03	8	\N	\N	\N	43
266	CHI	YOUSSEF	\N	\N	33313	H	P	\N	\N	2025-10-06	1992-01-01	8	\N	\N	\N	12
267	CHO	NAJIM	\N	\N	6863	H	O	\N	\N	2022-01-02	1993-11-22	8	\N	\N	\N	34
268	EL	ABDESLAM	\N	\N	30210	H	O	\N	\N	2023-12-17	1996-04-02	3	\N	\N	\N	28
269	EL	HASSAN	\N	\N	5627	H	O	\N	\N	2021-05-23	1993-08-04	8	\N	\N	\N	23
270	AJA	JAWAD	\N	\N	32036	H	O	\N	\N	2025-02-03	1998-08-28	8	\N	\N	\N	15
271	mou	Idriss	\N	\N	33032	H	P	\N	\N	2025-09-01	1998-02-25	8	\N	\N	\N	37
272	EL	MOHAMMED	\N	\N	30842	H	P	\N	\N	2024-07-08	1983-02-25	8	\N	\N	\N	30
273	Elb	Ibrahim	\N	\N	33909	H	P	\N	\N	\N	2002-03-28	4	\N	\N	\N	4
274	CHE	JAMAL AYOUB	\N	\N	32287	H	A	\N	\N	2025-06-02	2002-03-17	4	\N	\N	\N	41
275	EL-	SOUKAINA	\N	\N	8074	F	O	\N	\N	2022-11-11	2000-12-22	2	\N	\N	\N	9
276	HAM	ISMAIL	\N	\N	6709	H	A	\N	\N	2021-12-09	1994-05-31	8	\N	\N	\N	34
277	ASM	HAMZA	\N	\N	31185	H	P	\N	\N	2024-09-26	1992-06-25	1	\N	\N	\N	2
278	EL	MOSTAFA	\N	\N	5746	H	O	\N	\N	2021-07-05	1994-02-28	8	\N	\N	\N	17
279	KHA	SANAA	\N	\N	4717	H	E	\N	\N	2021-02-01	1994-09-24	8	\N	\N	\N	30
280	EL	SOUMIA	\N	\N	6772	F	O	\N	\N	2019-12-18	1999-10-11	8	\N	\N	\N	17
281	EL	ZAKARIA	\N	\N	32371	H	P	\N	\N	2025-06-16	2000-01-20	1	\N	\N	\N	22
282	EL	HAFID	\N	\N	7346	H	P	\N	\N	2022-07-03	1980-03-23	2	\N	\N	\N	19
284	OUA	ACHRAF	\N	\N	3640	H	P	\N	\N	2020-10-19	1997-02-28	8	\N	\N	\N	30
285	ABI	FATIMA EZZAHRAA	\N	\N	7250	H	A	\N	\N	2022-06-18	1995-02-03	8	\N	\N	\N	23
286	BAS	LOUAY	\N	\N	30637	H	A	\N	\N	2024-03-27	2002-11-19	8	\N	\N	\N	15
1050	LHA	KAOUTAR	\N	\N	6493	F	P	\N	\N	2021-11-06	1996-04-05	8	\N	\N	\N	44
1052	BEN	AMAL	\N	\N	30335	H	A	\N	\N	2024-01-24	2003-01-17	8	\N	\N	\N	30
1053	EL	OUISSAM	\N	\N	6499	H	O	\N	\N	2021-11-06	1993-07-25	3	\N	\N	\N	28
1054	AIT	YASSINE	\N	\N	32522	H	A	\N	\N	2025-06-05	1999-11-23	8	\N	\N	\N	30
1055	ELM	MOUAD	\N	\N	33906	H	E	\N	\N	2025-10-27	1998-05-20	9	\N	\N	\N	49
1056	ZER	ISSAM	\N	\N	5425	H	P	\N	\N	2021-04-24	1993-10-25	8	\N	\N	\N	17
1064	GHA	ADNANE	\N	\N	7818	H	O	\N	\N	2022-10-14	1998-08-16	8	\N	\N	\N	34
1065	DER	ABDELMOUNIM	\N	\N	3705	H	E	\N	\N	2020-11-02	1993-02-24	4	\N	\N	\N	4
1066	DIH	ARAFA	\N	\N	32481	H	E	\N	\N	2025-07-07	1994-06-15	8	\N	\N	\N	30
1067	MAR	OUALI	\N	\N	33903	H	P	\N	\N	2025-10-27	2001-08-12	2	\N	\N	\N	9
1068	KAI	ABDELMONIM	\N	\N	6298	H	O	\N	\N	2021-11-01	1990-02-06	8	\N	\N	\N	34
1069	EL-	CHAKIR	\N	\N	33482	H	E	\N	\N	2025-09-29	1991-11-19	8	\N	\N	\N	30
1070	BOU	MEHDI	\N	\N	9854	H	A	\N	\N	2023-09-26	2003-01-26	8	\N	\N	\N	30
1078	EL	MOHAMED	\N	\N	33397	H	P	\N	\N	2025-09-01	2002-05-23	8	\N	\N	\N	15
1081	LOU	TARIK	\N	\N	6145	H	O	\N	\N	2021-09-19	1987-01-02	8	\N	\N	\N	34
287	Zak	SADDIK	\N	\N	33050	H	P	\N	\N	2025-09-29	1991-03-22	2	\N	\N	\N	19
288	LHA	SOUFIANE	\N	\N	34022	H	P	\N	\N	2025-10-27	2000-07-26	8	\N	\N	\N	6
289	BOU	Zakaria	\N	\N	33026	H	P	\N	\N	2025-09-01	1992-07-07	4	\N	\N	\N	53
290	ESS	IMAD	\N	\N	31842	H	A	\N	\N	2024-11-22	1996-02-17	8	\N	\N	\N	34
292	EZZ	ABDELKABIR	\N	\N	30563	H	A	\N	\N	2024-03-14	2002-12-07	8	\N	\N	\N	15
293	STA	OTMANE	\N	\N	30445	H	E	\N	\N	2024-02-29	2000-09-07	8	\N	\N	\N	30
295	ZER	MOHAMED	\N	\N	30136	H	A	\N	\N	2023-11-30	1997-12-15	4	\N	\N	\N	4
296	SAJ	AYOUB	\N	\N	32281	H	A	\N	\N	2025-05-19	1992-05-16	1	\N	\N	\N	22
299	MES	EL HASSANE	\N	\N	655	H	P	\N	\N	2019-04-01	1991-04-19	8	\N	\N	\N	23
301	NOU	YASSER	\N	\N	32543	H	O	\N	\N	2025-06-02	2000-02-10	8	\N	\N	\N	34
302	OUA	YOUSSEF	\N	\N	31189	H	E	\N	\N	\N	1991-04-10	4	\N	\N	\N	41
304	HAI	SOUFIANE	\N	\N	32664	H	P	\N	\N	2025-07-03	1995-08-15	8	\N	\N	\N	43
307	HAN	MOHAMMED	\N	\N	31868	H	A	\N	\N	2024-12-05	2003-05-26	8	\N	\N	\N	15
309	GHA	NOUREDDINE	\N	\N	30584	H	P	\N	\N	2024-04-15	1988-12-24	5	\N	\N	\N	33
310	LAB	KHALID	\N	\N	9222	H	A	\N	\N	2023-06-21	1989-12-02	8	\N	\N	\N	44
311	ZAZ	ABDELHALIM	\N	\N	6853	H	O	\N	\N	2022-01-02	1987-06-21	8	\N	\N	\N	34
313	SRO	ISMAIL	\N	\N	31172	H	A	\N	\N	2024-09-19	1985-12-26	1	\N	\N	\N	22
315	ECH	ABDERRAHIM	\N	\N	8436	H	O	\N	\N	2022-12-24	1994-09-20	8	\N	\N	\N	15
316	Kah	SAFAE	\N	\N	31106	F	A	\N	\N	2024-09-04	2001-02-06	8	\N	\N	\N	44
317	ABI	BRAHIM	\N	\N	6537	H	A	\N	\N	2021-11-12	1995-08-29	8	\N	\N	\N	34
318	EL	MOHAMED	\N	\N	32695	H	P	\N	\N	2025-07-14	1998-06-21	8	\N	\N	\N	12
319	EL	MOHAMMED	\N	\N	30446	H	E	\N	\N	2022-09-11	1999-12-10	8	\N	\N	\N	30
320	LIQ	MOHAMED	\N	\N	33002	H	A	\N	\N	2025-07-31	2004-07-26	8	\N	\N	\N	15
324	BEN	FADL-ALLAH	\N	\N	2038	H	P	\N	\N	2019-12-12	1991-04-26	8	\N	\N	\N	30
326	EL	ABDELALI	\N	\N	2525	H	P	\N	\N	2020-03-12	1990-01-25	8	\N	\N	\N	15
327	ZAK	HAMZA	\N	\N	30403	H	A	\N	\N	2024-02-03	1998-10-10	8	\N	\N	\N	34
328	MOU	SMAIL	\N	\N	30254	H	P	\N	\N	2024-01-22	1986-04-22	8	\N	\N	\N	30
329	HAM	RAYAN	\N	\N	33897	H	P	\N	\N	\N	2005-11-04	4	\N	\N	\N	38
330	HAM	ABDENABI	\N	\N	8778	H	O	\N	\N	2023-02-23	1996-03-30	8	\N	\N	\N	15
331	EL	YOUSSEF	\N	\N	32372	H	P	\N	\N	2025-06-16	1994-10-16	4	\N	\N	\N	4
332	KAJ	IMAN	\N	\N	31792	H	O	\N	\N	2024-11-08	1999-08-28	8	\N	\N	\N	17
333	Abi	Otmane	\N	\N	5648	H	P	\N	\N	2021-05-30	1996-06-30	2	\N	\N	\N	52
334	IKK	YOUSSEF	\N	\N	7790	H	P	\N	\N	2022-10-12	1996-05-08	8	\N	\N	\N	6
335	HNA	YASSINE	\N	\N	30708	H	P	\N	\N	2024-05-13	1998-05-15	8	\N	\N	\N	23
336	EL	MEHDI	\N	\N	31979	H	P	\N	\N	2025-02-10	1996-03-11	8	\N	\N	\N	23
338	ZER	MOHAMMED	\N	\N	32546	H	O	\N	\N	2025-06-02	1998-01-27	8	\N	\N	\N	34
341	DER	AHMED	\N	\N	31903	H	P	\N	\N	2025-01-16	1999-03-19	8	\N	\N	\N	30
343	BOU	EL MAHDI	\N	\N	5454	H	P	\N	\N	2021-05-03	1994-08-27	8	\N	\N	\N	30
344	EL	ABDERRAHMAN	\N	\N	33911	H	P	\N	\N	2025-11-03	1998-10-14	8	\N	\N	\N	17
346	LAG	ABDERRAHIM	\N	\N	8437	H	O	\N	\N	2022-12-24	1996-08-04	8	\N	\N	\N	15
347	IDR	MOHAMED	\N	\N	9753	H	A	\N	\N	2023-09-14	1993-07-29	8	\N	\N	\N	30
348	EL	FOUZIA	\N	\N	196	F	C	\N	\N	2018-03-15	1976-07-02	8	\N	\N	\N	15
1083	ADA	WAFA	\N	\N	8511	H	A	\N	\N	2022-12-31	1995-07-04	8	\N	\N	\N	6
1085	ATT	ANAS	\N	\N	32308	H	A	\N	\N	2025-05-02	2002-05-01	8	\N	\N	\N	12
1086	ZEG	AMINE	\N	\N	6133	H	P	\N	\N	2019-09-18	1999-02-23	8	\N	\N	\N	23
1087	AIT	YOUSSEF	\N	\N	7118	H	P	\N	\N	2020-03-04	1994-09-25	8	\N	\N	\N	34
1088	KHA	MOHAMMED	\N	\N	9436	H	A	\N	\N	2023-08-09	1998-03-30	8	\N	\N	\N	15
1090	ALI	MOHAMED	\N	\N	32768	H	A	\N	\N	2025-07-08	2007-01-15	8	\N	\N	\N	15
1091	ANA	TOUIL	\N	\N	33484	H	P	\N	\N	2025-10-27	1989-08-08	2	\N	\N	\N	9
1092	BLI	Nour-Eddine	\N	\N	33469	H	P	\N	\N	2025-10-20	1997-06-17	8	\N	\N	\N	12
349	AAK	OTHMANE	\N	\N	5908	H	O	\N	\N	2021-07-24	1992-08-11	8	\N	\N	\N	23
351	EL-	FISSAL	\N	\N	954	H	A	\N	\N	2019-06-10	1990-08-12	8	\N	\N	\N	6
352	HAM	REDA	\N	\N	7580	H	O	\N	\N	2022-09-15	1997-04-18	8	\N	\N	\N	34
353	DRI	Mounir	\N	\N	33315	H	A	\N	\N	2025-10-06	1994-11-27	8	\N	\N	\N	17
354	SAI	HAMZA	\N	\N	31849	H	O	\N	\N	2024-11-22	1999-01-13	8	\N	\N	\N	30
355	ZAH	YOUNES	\N	\N	30632	H	A	\N	\N	2024-03-25	2000-03-16	8	\N	\N	\N	34
357	BOU	AZIZ	\N	\N	6108	H	A	\N	\N	2021-09-04	1994-04-12	8	\N	\N	\N	30
359	YAT	YASSINE	\N	\N	6720	H	O	\N	\N	2021-12-11	1995-12-19	8	\N	\N	\N	6
360	EL	RACHID	\N	\N	3022	H	C	\N	\N	2020-09-01	1983-01-01	8	\N	\N	\N	30
361	AMA	RIDA	\N	\N	6289	H	O	\N	\N	2021-10-18	2000-02-16	4	\N	\N	\N	53
363	AMZ	MOHAMED	\N	\N	8507	H	A	\N	\N	2022-12-28	1996-07-08	8	\N	\N	\N	34
364	ELA	RACHID	\N	\N	32175	H	P	\N	\N	2025-04-28	1987-06-02	8	\N	\N	\N	6
365	AHM	MOHAMMED	\N	\N	7155	H	A	\N	\N	2022-03-16	1993-10-04	8	\N	\N	\N	15
366	BAR	OMAR	\N	\N	8088	H	P	\N	\N	2022-11-18	2000-01-01	2	\N	\N	\N	39
367	AIT	MOHAMED	\N	\N	30441	H	E	\N	\N	2024-03-04	1993-11-17	7	\N	\N	\N	20
370	LAK	Abedelali	\N	\N	33041	H	E	\N	\N	2025-09-08	2001-05-24	10	\N	\N	\N	10
371	EL	AYOUB	\N	\N	31535	H	A	\N	\N	2024-10-17	1996-08-26	8	\N	\N	\N	6
372	FTO	Youssef	\N	\N	33049	H	A	\N	\N	2025-09-22	2000-10-02	8	\N	\N	\N	43
373	EL	ALI	\N	\N	8256	H	O	\N	\N	2022-12-01	1997-07-17	2	\N	\N	\N	35
375	BAR	ANAS	\N	\N	33496	H	E	\N	\N	2025-10-27	1995-07-10	9	\N	\N	\N	49
377	SOU	AHMED AMINE	\N	\N	32263	H	P	\N	\N	2025-05-05	1999-06-16	1	\N	\N	\N	24
378	IDR	YOUNESS	\N	\N	5649	H	O	\N	\N	2021-05-30	1996-03-04	8	\N	\N	\N	6
381	ZOU	El Mehdi	\N	\N	33031	N	A	\N	\N	2025-09-29	1994-04-19	8	\N	\N	\N	15
382	AHM	SANAA	\N	\N	30897	N	A	\N	\N	2024-08-11	2000-12-16	8	\N	\N	\N	31
383	ERR	MOHAMED	\N	\N	30527	H	P	\N	\N	2024-03-21	1995-01-13	8	\N	\N	\N	12
384	JAM	LYAZGHI	\N	\N	6491	H	P	\N	\N	2021-11-06	1991-12-05	2	\N	\N	\N	19
386	BOU	MAROUANE	\N	\N	30664	H	E	\N	\N	2024-04-15	1993-08-28	8	\N	\N	\N	12
387	EL	MOHAMMED	\N	\N	30131	H	A	\N	\N	2023-11-30	1997-02-15	8	\N	\N	\N	17
389	AHM	KACHADE	\N	\N	33483	H	P	\N	\N	2025-10-13	2000-12-26	2	\N	\N	\N	9
390	BEK	KAMAL	\N	\N	31187	H	A	\N	\N	2024-09-23	1997-05-15	8	\N	\N	\N	30
391	EL	MOHAMMED	\N	\N	5255	H	P	\N	\N	2021-03-20	1994-07-12	8	\N	\N	\N	23
392	HDO	MOUHCINE	\N	\N	32273	H	P	\N	\N	2025-05-19	1992-01-14	1	\N	\N	\N	22
394	KAD	MOHAMMED	\N	\N	233	H	P	\N	\N	\N	2018-04-09	8	\N	\N	\N	23
395	EL	FARES	\N	\N	9976	H	P	\N	\N	2023-10-19	1994-01-15	8	\N	\N	\N	30
396	Lij	Hamza	\N	\N	31793	H	A	\N	\N	2024-11-08	1995-12-22	4	\N	\N	\N	53
398	LAR	SALIM	\N	\N	5082	H	O	\N	\N	2021-03-06	1991-12-01	8	\N	\N	\N	6
400	IDR	BADRE	\N	\N	32280	H	P	\N	\N	2025-05-19	1991-01-18	8	\N	\N	\N	43
401	JAL	SOUFIANE	\N	\N	5609	H	O	\N	\N	2021-06-19	1997-06-07	2	\N	\N	\N	9
402	EL	SARA	\N	\N	32346	H	A	\N	\N	2025-05-08	1996-08-28	1	\N	\N	\N	14
403	MOH	OIZANI	\N	\N	7152	H	P	\N	\N	2022-03-16	1987-03-04	2	\N	\N	\N	39
404	LOU	DRISS	\N	\N	30983	H	E	\N	\N	2024-09-02	1993-07-07	1	\N	\N	\N	22
406	ZAR	MOHAMED	\N	\N	30382	H	O	\N	\N	2024-01-26	2000-06-26	8	\N	\N	\N	34
408	ELK	YASSINE	\N	\N	8510	H	O	\N	\N	2022-12-30	1997-07-25	8	\N	\N	\N	34
409	LAM	SOUKAINA	\N	\N	297	F	\N	\N	\N	2018-05-24	1991-04-23	7	\N	\N	\N	51
410	ADI	ELFARJI	\N	\N	7103	H	P	\N	\N	2022-02-26	1997-07-01	2	\N	\N	\N	13
411	ARA	OUSSAMA	\N	\N	32114	H	P	\N	\N	2025-03-10	2001-12-10	1	\N	\N	\N	14
414	HBI	RABIAE	\N	\N	7581	F	O	\N	\N	2022-09-25	1994-02-24	8	\N	\N	\N	34
751	EL	MOURAD	\N	\N	7304	H	P	\N	\N	2022-06-25	1996-07-21	8	\N	\N	\N	44
1046	El	Mustapha	\N	\N	391	H	A	\N	\N	\N	\N	8	\N	\N	\N	34
1093	ZRI	OUSSAMA	\N	\N	32319	H	P	\N	\N	2025-05-07	2001-09-14	8	\N	\N	\N	34
1095	Bar	jawad	\N	\N	31774	H	P	\N	\N	2022-11-01	1994-03-18	8	\N	\N	\N	15
1097	ELM	AYOUB	\N	\N	30212	H	P	\N	\N	2023-12-17	1999-04-07	8	\N	\N	\N	30
1103	REZ	YASSINE	\N	\N	32035	H	P	\N	\N	2023-02-03	2003-05-09	8	\N	\N	\N	15
1104	ELA	ACHRAF	\N	\N	31347	H	P	\N	\N	2024-10-07	1997-02-07	8	\N	\N	\N	43
1105	AIT	ZAKARIA	\N	\N	5373	H	P	\N	\N	2021-04-24	1996-02-08	8	\N	\N	\N	30
1106	NEJ	MOURAD	\N	\N	31972	F	E	\N	\N	2025-01-20	1982-06-06	2	\N	\N	\N	39
\.


--
-- Data for Name: entreprise; Type: TABLE DATA; Schema: public; Owner: s3m_user
--

COPY public.entreprise (id_entreprise, nom_entreprise) FROM stdin;
3	AFTL
4	S3M
5	ONE
6	SGS
7	GTH CONSULT
8	IFMIA
9	PCI SERVICES
10	DURR
12	Client Test
2	Stellantis
1	STELLANTIS
15	TEST_ENTREPRISE_2
\.


--
-- Data for Name: evaluation_a_chaud; Type: TABLE DATA; Schema: public; Owner: s3m_user
--

COPY public.evaluation_a_chaud (id_eval_chaud, id_participation, evaluation_a_chaud) FROM stdin;
\.


--
-- Data for Name: evaluation_a_froid; Type: TABLE DATA; Schema: public; Owner: s3m_user
--

COPY public.evaluation_a_froid (id_eval_froid, id_participation, id_n_plus_1, evaluation_participant, evaluation_n_plus_1, date_evaluation_a_froid, taux_efficacite) FROM stdin;
4	1020	2	Good	Very Good	2025-12-20	85.00
5	1019	2	Excellent	Excellent	2025-12-22	90.00
\.


--
-- Data for Name: fiche_technique_formation; Type: TABLE DATA; Schema: public; Owner: s3m_user
--

COPY public.fiche_technique_formation (id_fiche, id_formation, version_numero, statut, date_creation, date_activation, date_archivage, description, objectifs, competences_cible, prerequis, population_cible, programme, nb_participants_min, nb_participants_max, duree_jours, duree_heures, modalites_evaluation, indicateurs_succes) FROM stdin;
1	21	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
2	37	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
3	53	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
4	81	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
6	3	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
9	6	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
12	9	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Comprendre les causes racines des défaillances, comment les identifier et les réduire	Appliquer correctement les actions de la maintenance préventive\n- Réparer les équipements d’une manière efficace 	Avoir des Connaissances en électromécanique	Maintenanciers 	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	Réduction du MTTR\n• Amélioration du MTBF
13	10	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
45	44	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
52	51	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
55	55	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	Aucun 	\N	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
14	11	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Conduire et exploiter un robot ABB Baie IRC5.\nDiagnostiquer des anomalies de fonctionnement à partir des messages de l’interface Homme Machine (IHM).\nRedémarrer le moyen en toute sécurité\n	Conduire et exploiter un robot ABB Baie IRC5.\nDiagnostiquer des anomalies de fonctionnement à partir des messages de l’interface Homme Machine (IHM).\nRedémarrer le moyen en toute sécurité\n	Aucun 	\N	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	1 Accicent/Incident du à la mauvaise manipulation d'engin 
15	12	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
16	13	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
17	14	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Cette formation permettra au bénéficiaire d’être\ncapable d’acquérir les compétences théoriques et\npratiques pour une conduite sécuritaire,\ncomprendre le fonctionnement des principaux\norganes et équipements pour une conduite\nsécuritaire et réaliser en sécurité les opérations de\nmanutention prescrites, en impliquant la mise en\nœuvre du chariot élévateur à conducteur porté.	Prise de poste et vérification.\nConduite en sécurité et manœuvres.	Aucun 	Conducteurs d'engins	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	0 Accicent/Incident du à la mauvaise manipulation d'engin 
18	15	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	Aucun 	\N	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
19	16	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
20	17	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	Aucun 	\N	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
83	83	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
84	84	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
86	86	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
87	87	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
88	88	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
5	2	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Etre en mesure de maitriser le principe de scrutateur laser en toute sécurité 	Etre en mesure de maitriser le principe de scrutateur laser en toute sécurité 	Principes de base de l'automatisme et de la robotique 	Mantenanciers 	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
7	4	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Préparer à l&#39;habilitation électrique BR (Personnel chargé d’interventions d’entretien\net de dépannage).\nConnaître les méthodes et procédures à mettre en œuvre pour intervenir sur les\ninstallations électriques dans les meilleures conditions de sécurité et de continuité\nde service.\nPermettre à l’employeur de délivrer un titre d’habilitation électrique en adéquation\navec l’activité de l’agent.\n Effectuer une analyse préalable à l’intervention Identifier et analyser les risques\nélectriques dans un local, une armoire ou en champ libre Identifier, repérer et mettre\nen œuvre les EPI et les EPC.\nRéaliser une intervention d’entretien et de dépannage et rédiger les documents\nnécessaires.	Analyser les risques dans une situation donnée.\n Utiliser le matériel et les équipements de protection et connaître leurs limites\nd’utilisation\n Les fonctions des matériels électriques des domaines de tension BT et TBT.\n Réaliser des séquences de la mise en sécurité d’un circuit et les mesures de\nprévention.\n Réaliser une procédure de consignation et réaliser une vérification d’absence de\ntension (VAT).\n Les documents applicables dans le cadre des interventions de remplacement et de\nraccordement.\n Réaliser la procédure de remplacement et la procédure de raccordement.\n L’analyse de risques pour une situation donnée et correspondant à l’habilitation\nvisée (BR), Tout en mettant en œuvre les prescriptions propres à chaque opération\net en respectant les procédures d’intervention.\n Réaliser des opérations de consignation et déconsignation pour lui-même.\n Réaliser des opérations de dépannage, de mesurage et d’essai.\n Réaliser des opérations de connexion et de déconnexion en présence ou non de\ntension (inférieure à 500 V)	Connaissances de base en maintenance éléctrique 	Electriciens	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	0 Accident/ Incident dû à une manipulation éléctrique 
8	5	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	A l’issu de cette formation, les bénéficiaires seront capables de :\n► Comprendre les principes de base de la sécurité machine.\n► Identifier et câbler les relais de sécurité PILZ.\n► Configurer et mettre en service des systèmes de sécurité avec PNOZmulti.\n► Développer des applications de sécurité avancées avec le système PSS\n4000 et le logiciel PAS4000.\n► Diagnostiquer et dépanner les systèmes de sécurité basés sur les relais et\nPLCs PILZ	A l’issu de cette formation, les bénéficiaires seront capables de :\n► Comprendre les principes de base de la sécurité machine.\n► Identifier et câbler les relais de sécurité PILZ.\n► Configurer et mettre en service des systèmes de sécurité avec PNOZmulti.\n► Développer des applications de sécurité avancées avec le système PSS\n4000 et le logiciel PAS4000.\n► Diagnostiquer et dépanner les systèmes de sécurité basés sur les relais et\nPLCs PILZ	Principes de base de l'automatisme et de la robotique 	Mantenanciers 	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
10	7	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
11	8	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Avoir les connaissances nécessaires pour les intervention BT en toute sécurité	Participant capable d'assurer des tâches d'ordre non-électrique et\nsouhaitant veiller à sa propre sécurité, ainsi que celle des autres. \nMaitrise des dangers d'ordre électrique et les bonnes pratiques en\nmatière de sécurité et de prévention	Aucun 	Electriciens	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	0 Accident/ Incident dû à une manipulation 
21	18	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	À l'issue de ce e forma on, les par cipants seront capables de : \n1. Comprendre le fontionnement des variateurs de vitesse, en par culier le modèle Siemens \nMicroMaster 420. \n2. Configurer et paramétrer un variateur Siemens MicroMaster 420. \n3. Diagnos quer les erreurs courantes et résoudre les problèmes associés. \n4. Savoir intégrer le variateur dans un système d’automa sa on : Câblage. \n5. Opmiser les performances du variateur en fonc on des besoins spécifiques de l'applica on.	Maitriser le fonctionnement des variateurs SIEMES MICROMASTER 420\nConfigurer et paramétrer un variateur Siemens MicroMaster 420\nDiagnos quer les erreurs courantes et résoudre les problèmes associés. \nSavoir intégrer le variateur dans un système d’automa sa on : Câblage\nOpmiser les performances du variateur en fonc on des besoins spécifiques de l'applica on.. \n	Avoir des connaissances en électromécanique, et variateurs 	Techniciens de maintenance  Ingénieurs en automa sa on  Opérateurs et u lisateurs de variateurs de vitesse	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	0 Panne Technique 
22	19	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Comprendre les bases des normes ISO 9001 et 19001	Comprendre les bases des normes ISO 9001 et 19001	Aucun 	Personne ayant à intervenir dans le domaine de la qualité	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
23	20	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Identifier les principaux composants mécaniques du robot,\n► Réaliser des vérifications mécaniques périodiques (jeux, graissage,\nalignement),\n► Intervenir en sécurité sur les éléments mécaniques du robot,\n► Diagnostiquer les pannes mécaniques les plus courantes.	Identifier les principaux composants mécaniques du robot,\n► Réaliser des vérifications mécaniques périodiques (jeux, graissage,\nalignement),\n► Intervenir en sécurité sur les éléments mécaniques du robot,\n► Diagnostiquer les pannes mécaniques les plus courantes.	Principes de base de l'automatisme et de la robotique 	Mantenanciers 	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
24	22	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Cette formation permet d'acquérir les compétences nécessaires à la conduite d'un pont roulant en toute sécurité, de recycler ses connaissances et se perfectionner en matière de conduite en sécurité.	Cette formation permettra au participant d’acquérir les connaissances des risques qui y sont reliés et doit être en mesure de les contrôler afin d’éviter les situations problématiques ou dangereuses	Aucun 	Toute personne débutante ou expérimentée devant réaliser des opérations à l’aide d’un pont roulant 	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	0 Accicent/Incident du à la mauvaise manipulation d'engin 
25	23	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	L’objectif principal de la formation IRCA 9001 est de doter les participants des connaissances et des compétences essentielles pour réaliser des audits qui répondent de manière optimale aux exigences de la norme ISO 9001.	L’objectif principal de la formation IRCA 9001 est de doter les participants des connaissances et des compétences essentielles pour réaliser des audits qui répondent de manière optimale aux exigences de la norme ISO 9001.	Aucun 	Pilotes de processus 	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
26	24	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Avoir l'aptitude de conduire les nacelle en toute sécurité	Appliquer les règles générales de sécurité lors de la conduite de nacelle \nChoisir les moyens de protection appropriés.	Aucun 	Conducteurs Nacelle	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	0 Accident/ Incident dû à une manipulation 
27	25	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
28	26	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	permettre au bénéficiaire d’être capable d’intervenir\nefficacement face à une situation d’accident et ainsi de mettre ses\ncompétences au profit de la santé et sécurité au travail, dans le\nrespect des procédures fixées par l’entreprise en matière de\nprévention	Maitriser les régles de secourisme \nCapacité à  porter les premiers secours à toute victime d'un\naccident du travail ou d'un malaise,\nCapacité à jouer le rôle d'acteur de la\nprévention dans l'entreprise	Aucun 	Secouristes 	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	0 Accicent/Incident 
29	27	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
30	28	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Former aux règles et procédures pour prévenir les accidents d’origine électrique	Etre capable de maitriser les règles et procédures pour prévenir les accidents d’origine électrique	Bases de maintenance	Techniciens de maintenance 	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
31	29	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Avoir l'aptitude de réaliser des travaux en hauteur et conduire les nacelle en toute sécurité	Appliquer les règles générales de sécurité lors de travaux en\nhauteur,\n Évaluer les risques de chute de hauteur sur différents postes de\ntravail,\nChoisir les moyens de protection appropriés.	Aucun 	Personnes travaillant en hauteur\nConducteurs machines 	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	Participant apte à appréhender\nles mesures et pratiques de sécurité pour assurer la prévention\nd’incidents ou d’accidents
32	30	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	À l'issue de ce e forma on, les par cipants seront capables de : \n1. Comprendre les principes fondamentaux des réseaux locaux industriels (RLI) et leur rôle dans \nl’automa sa on industrielle. \n2. Iden fier les différences et les avantages des réseaux PROFIBUS, PROFINET et ASI Bus. \n3. Concevoir, installer et configurer des réseaux industriels u lisant ces technologies. \n4. Diagnos quer et résoudre des pannes et erreurs courantes sur ces réseaux. \n5. Intégrer ces réseaux dans un environnement automa sé en u lisant des ou ls adaptés \n(commutateurs, PLC, terminaux, etc.). 	À l'issue de ce e forma on, les par cipants seront capables de : \n1. Comprendre les principes fondamentaux des réseaux locaux industriels (RLI) et leur rôle dans \nl’automa sa on industrielle. \n2. Iden fier les différences et les avantages des réseaux PROFIBUS, PROFINET et ASI Bus. \n3. Concevoir, installer et configurer des réseaux industriels u lisant ces technologies. \n4. Diagnos quer et résoudre des pannes et erreurs courantes sur ces réseaux. \n5. Intégrer ces réseaux dans un environnement automa sé en u lisant des ou ls adaptés \n(commutateurs, PLC, terminaux, etc.). 	Bases de maintenance	Ingénieurs et techniciens en automa sa on et contrôle \n Responsables de la maintenance des réseaux industriels \n Techniciens réseau et en communica on industrielle	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
50	49	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Maîtriser les fonctions avancées des variateurs Power Flex\n► Intégrer le variateur dans un système automatisé via Ethernet/IP\n► Diagnostiquer et résoudre des pannes complexe.\n► Effectuer la maintenance préventive et corrective.	Maîtriser les fonctions avancées des variateurs Power Flex\n► Intégrer le variateur dans un système automatisé via Ethernet/IP\n► Diagnostiquer et résoudre des pannes complexe.\n► Effectuer la maintenance préventive et corrective.	Principes de base de l'automatisme et de la robotique 	Mantenanciers 	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
33	31	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	À l'issue de ce e forma on, les par cipants seront capables de : \n1. Comprendre les principes fondamentaux des réseaux locaux industriels (RLI) et leur rôle dans \nl’automa sa on industrielle. \n2. Iden fier les différences et les avantages des réseaux PROFIBUS, PROFINET et ASI Bus. \n3. Concevoir, installer et configurer des réseaux industriels u lisant ces technologies. \n4. Diagnos quer et résoudre des pannes et erreurs courantes sur ces réseaux. \n5. Intégrer ces réseaux dans un environnement automa sé en u lisant des ou ls adaptés \n(commutateurs, PLC, terminaux, etc.). 	1. Comprendre les principes fondamentaux des réseaux locaux industriels (RLI) et leur rôle dans \nl’automa sa on industrielle. \n2. Iden fier les différences et les avantages des réseaux PROFIBUS, PROFINET et ASI Bus. \n3. Concevoir, installer et configurer des réseaux industriels u lisant ces technologies. \n4. Diagnos quer et résoudre des pannes et erreurs courantes sur ces réseaux. \n5. Intégrer ces réseaux dans un environnement automa sé en u lisant des ou ls adaptés \n(commutateurs, PLC, terminaux, etc.). 	Connaissances de base en automatisme et maintenance industrielle 	Ingénieurs et techniciens en automa sa on et contrôle \n Responsables de la maintenance des réseaux industriels \n Techniciens réseau et en communica on industrielle	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
34	32	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
35	33	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
36	34	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
37	35	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
38	36	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	Aucun 	\N	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	0 Accident/ Incident dû à une manipulation 
39	38	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Savoir changer et paramétrer une imprimante/parmétrer un switch/ changer un PC Leger/configurer le scan code a barre	Savoir changer et paramétrer une imprimante/parmétrer un switch/ changer un PC Leger/configurer le scan code a barre	Aucun 	\N	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
40	39	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
41	40	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	permettre au bénéficiaire d’être capable d’intervenir\nefficacement face à une situation d’accident et/Ou face à une incendie et ainsi de mettre ses\ncompétences au profit de la santé et sécurité au travail, dans le\nrespect des procédures fixées par l’entreprise en matière de\nprévention	Maitriser les régles de secourisme \nCapacité à  porter les premiers secours à toute victime d'un\naccident du travail ou d'un malaise,\nCapacité à jouer le rôle d'acteur de la\nprévention dans l'entreprise	Aucun 	\N	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	respect des standards HSE à 100%
42	41	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Avoir les connaissances nécessaires pour les intervention au niveau des installation gaz en toute sécurité, Fonctionnement & Diagnostic & Réparation	Participant capable d'intervenir immédiatement au niveau des installation de gaz en évitant tout accident et/ou incident	Aucun 	\N	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	0 Accident/ Incident dû à une manipulation 
43	42	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
44	43	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Comprendre l'architecture d'un système automatisé ; \n Identifier les composants d’un système automatisé ; \n Connaître la structure d'un API M340 et TSX PREMIUM ; \n Comprendre le principe de fonctionnement d’un API ; \n Savoir programmer sous Unity Pro XL ; \n Utiliser Unity Pro pour piloter un automate Schneider M340 ; \n Acquérir les bases du diagnostic d’un automate programmable. 	Comprendre l'architecture d'un système automatisé ; \n Identifier les composants d’un système automatisé ; \n Connaître la structure d'un API M340 et TSX PREMIUM ; \n Comprendre le principe de fonctionnement d’un API ; \n Savoir programmer sous Unity Pro XL ; \n Utiliser Unity Pro pour piloter un automate Schneider M340 ; \n Acquérir les bases du diagnostic d’un automate programmable. 	Aucun 	\N	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
46	45	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	A l’issu de cette formation, les bénéficiaires seront capables de :Faire des démonstrations,\net des cas pratiques sur le matériel Siemens,\n	Etre capable de démonstrer faire des cas pratiques  sur le matériel Siemens,	Maitrise des bases de maintenance - Automatisme	\N	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	0 Incidents/Accidents techniques 
47	46	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	À l'issue de ce e forma on, les par cipants seront capables de : 1. Comprendre le principe de fonc onnement d’un variateur de vitesse. 2. Effectuer les paramétrages de base et avancés du variateur. 3. Diagnos quer et résoudre les problèmes courants rencontrés sur les variateurs. 4. Savoir u liser les fonc onnalités avancées et les modes de commande du variateur pour des applica ons spécifiques. 5. Intégrer et configurer le variateur dans un environnement d'automa sa on industrielle.	À l'issue de ce e forma on, les par cipants seront capables de : 1. Comprendre le principe de fonc onnement d’un variateur de vitesse. 2. Effectuer les paramétrages de base et avancés du variateur. 3. Diagnos quer et résoudre les problèmes courants rencontrés sur les variateurs. 4. Savoir u liser les fonc onnalités avancées et les modes de commande du variateur pour des applica ons spécifiques. 5. Intégrer et configurer le variateur dans un environnement d'automa sa on industrielle.	Avoir des connaissances en électromécanique, et variateurs 	Techniciens de maintenance  Ingénieurs en automa sa on  Opérateurs et u lisateurs de variateurs de vitesse	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
48	47	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	permettre au bénéficiaire d’être capable d’intervenir\nefficacement face à une situation d’accident et ainsi de mettre ses\ncompétences au profit de la santé et sécurité au travail, dans le\nrespect des procédures fixées par l’entreprise en matière de\nprévention	Maitriser les régles de secourisme \nCapacité à  porter les premiers secours à toute victime d'un\naccident du travail ou d'un malaise,\nCapacité à jouer le rôle d'acteur de la\nprévention dans l'entreprise	Aucun 	\N	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	respect des standards HSE à 100%
49	48	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Cette formation permettra au bénéficiaire d’être\ncapable d’acquérir les compétences théoriques et\npratiques pour une conduite sécuritaire,\ncomprendre le fonctionnement des principaux\norganes et équipements pour une conduite\nsécuritaire et réaliser en sécurité les opérations de\nmanutention prescrites, en impliquant la mise en\nœuvre du chariot élévateur à conducteur porté.	Prise de poste et vérification.\nConduite en sécurité et manœuvres.	Aucun 	Conducteurs d'engins	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	1 Accicent/Incident du à la mauvaise manipulation d'engin 
51	50	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Cette formation permettra au bénéficiaire d’être\ncapable d’acquérir les compétences théoriques et\npratiques pour une conduite sécuritaire,\ncomprendre le fonctionnement des principaux\norganes et équipements pour une conduite\nsécuritaire et réaliser en sécurité les opérations de\nmanutention prescrites, en impliquant la mise en\nœuvre du chariot élévateur à conducteur porté.	Prise de poste et vérification.\nConduite en sécurité et manœuvres.	Aucun 	Conducteurs d'engins	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	6 Accicent/Incident du à la mauvaise manipulation d'engin 
53	52	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Comprendre le fonctionnement des variateurs de vitesse, en particulier le modèle NIDEC LEROYSOMER. \n►Configureretparamétrer un variateur Siemens NIDEC LEROY SOMER. \n►Diagnostiquer les erreurs courantes et résoudre les problèmes associés. \n►Savoir intégrer le variateur dans un système d’automatisation : Câblage. ►Optimiserlesperformancesduvariateurenfonctiondesbesoinsspécifiquesde l'application	Comprendre le fonctionnement des variateurs de vitesse, en particulier le modèle NIDEC LEROYSOMER. \n►Configureretparamétrer un variateur Siemens NIDEC LEROY SOMER. \n►Diagnostiquer les erreurs courantes et résoudre les problèmes associés. \n►Savoir intégrer le variateur dans un système d’automatisation : Câblage. ►Optimiserlesperformancesduvariateurenfonctiondesbesoinsspécifiquesde l'application	Connaissances de base en maintenance industrielle et automatisme	Ingénieurs et techniciens en automa sa on et contrôle \n Responsables de la maintenance des réseaux industriels \n Techniciens réseau et en communica on industrielle	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
54	54	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	A l’issu de cette formation, les bénéficiaires seront capables de :\n► Démarrer et arrêter un robot FANUC en sécurité\n► Utiliser le Teach-pendant pour naviguer et exécuter des programmes\n► Lire, interpréter et modifier un programme simple\n► Gérer les modes de fonctionnement, les alarmes, et les cycles automatiques	A l’issu de cette formation, les bénéficiaires seront capables de :\n► Démarrer et arrêter un robot FANUC en sécurité\n► Utiliser le Teach-pendant pour naviguer et exécuter des programmes\n► Lire, interpréter et modifier un programme simple\n► Gérer les modes de fonctionnement, les alarmes, et les cycles automatiques	Principes de base de l'automatisme et de la robotique 	Mantenanciers 	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
56	56	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	Prise de poste et vérification.\nConduite en sécurité et manœuvres.	Aucun 	Conducteurs d'engins	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	0 Accicent/Incident du à la mauvaise manipulation d'engin 
57	57	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Cette formation permettra au bénéficiaire d’être\ncapable d’acquérir les compétences théoriques et\npratiques pour une conduite sécuritaire,\ncomprendre le fonctionnement des principaux\norganes et équipements pour une conduite\nsécuritaire et réaliser en sécurité les opérations de\nmanutention prescrites, en impliquant la mise en\nœuvre du chariot élévateur à conducteur porté.	Prise de poste et vérification.\nConduite en sécurité et manœuvres.	Aucun 	Conducteurs d'engins	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	0 Accicent/Incident du à la mauvaise manipulation d'engin 
58	58	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
59	59	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Permettre au bénéficiare d'être capable de Maitriser : \n•Les principes de base des variateurs de vitesse \n• Fonction des variateurs de vitesse\n • Le redresseur\n • Le circuit intermédiaire\n • L'onduleur\n • Modes de fonctionnement de l'onduleur \n• Le circuit de commande\n • L'optimisation automatique de l'énergie	À l'issue de ce e forma on, les par cipants seront capables de : \n1. Comprendre le fonc onnement du variateur de vitesse Sew. \n2. Configurer et paramétrer le variateur pour diverses applica ons industrielles. \n3. Diagnos quer les erreurs courantes et effectuer des actions de maintenance de base. \n4. Me re en place des stratégies de contrôle et d'opmisa on des performances du variateur. \n5. Intégrer le variateur SEW dans des systèmes d'automa sa on industrielle	Avoir des connaissances en électromécanique, et variateurs 	Techniciens de maintenance \n Ingénieurs en automa sa on \n Opérateurs et u lisateurs de variateurs de vitesse	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
60	60	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Cette formation permettra au bénéficiaire d’être\ncapable d’acquérir les compétences théoriques et\npratiques pour une conduite sécuritaire,\ncomprendre le fonctionnement des principaux\norganes et équipements pour une conduite\nsécuritaire et réaliser en sécurité les opérations de\nmanutention prescrites, en impliquant la mise en\nœuvre du chariot élévateur à conducteur porté.	Prise de poste et vérification.\nConduite en sécurité et manœuvres.	Aucun 	Conducteurs d'engins	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	4 Accicent/Incident du à la mauvaise manipulation d'engin 
61	61	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
62	62	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N
63	63	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Savoir changer et paramétrer une imprimante/parmétrer un switch/ changer un PC Leger/configurer le scan code a barre	Savoir changer et paramétrer une imprimante/parmétrer un switch/ changer un PC Leger/configurer le scan code a barre	Connaissances de base en informatique 	\N	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
64	64	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Cette formation permettra au bénéficiaire d’être\ncapable d’acquérir les compétences théoriques et\npratiques pour une conduite sécuritaire,\ncomprendre le fonctionnement des principaux\norganes et équipements pour une conduite\nsécuritaire et réaliser en sécurité les opérations de\nmanutention prescrites, en impliquant la mise en\nœuvre du chariot élévateur à conducteur porté.	Prise de poste et vérification.\nConduite en sécurité et manœuvres.	Aucun 	Ingénieurs et techniciens en automa sa on et contrôle \n Responsables de la maintenance des réseaux industriels \n Techniciens réseau et en communica on industrielle	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	0 Accicent/Incident du à la mauvaise manipulation d'engin 
65	65	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	Aucun 	\N	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
66	66	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	Aucun 	\N	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
67	67	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	Aucun 	\N	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
68	68	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	Connaissances de base en maintenance industrielle et automatisme	Roboticiens 	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
69	69	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Savoir changer et paramêtrer une imprimante \nSavoir changer et paramêtrer un switch\nSavoir changer un PC Léger \nSavoir changer et configurer le scan code à barre 	Savoir changer et paramêtrer une imprimante \nSavoir changer et paramêtrer un switch\nSavoir changer un PC Léger \nSavoir changer et configurer le scan code à barre 	Aucun 	\N	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
70	70	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Permettre au bénéficiare d'être capable de Maitriser : \n•Les principes de base des variateurs de vitesse \n• Fonction des variateurs de vitesse\n • Le redresseur\n • Le circuit intermédiaire\n • L'onduleur\n • Modes de fonctionnement de l'onduleur \n• Le circuit de commande\n • L'optimisation automatique de l'énergie	À l'issue de ce e forma on, les par cipants seront capables de : \n1. Comprendre le fonc onnement du variateur de vitesse Sew. \n2. Configurer et paramétrer le variateur pour diverses applica ons industrielles. \n3. Diagnos quer les erreurs courantes et effectuer des ac ons de maintenance de base. \n4. Me re en place des stratégies de contrôle et d'opmisa on des performances du variateur. \n5. Intégrer le variateur SEW dans des systèmes d'automa sa on industrielle	Avoir des connaissances en électromécanique, et variateurs 	Techniciens de maintenance \n Ingénieurs en automa sa on \n Opérateurs et u lisateurs de variateurs de vitesse	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
71	71	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Conduire et exploiter un robot ABB Baie IRC5.\nDiagnostiquer des anomalies de fonctionnement à partir des messages de l’interface Homme Machine (IHM).\nRedémarrer le moyen en toute sécurité\n	Conduire et exploiter un robot ABB Baie IRC5.\nDiagnostiquer des anomalies de fonctionnement à partir des messages de l’interface Homme Machine (IHM).\nRedémarrer le moyen en toute sécurité\n	Aucun 	\N	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	0 Accicent/Incident du à la mauvaise manipulation d'engin 
72	72	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Cette formation permet d’identifier les mécanismes de l’explosion et d’adapter son comportement pour travailler en sécurité sur des installations électriques en atmosphère explosif (ATEX) en conformité avec les directifs ATEX en vigueur 	Cette formation permettra au bénéficiaire d’être capable d’identifier les zones ATEX,sensibiliser par rapport aux risques liés à la zone ATEX et maitriser les moyens de prévention et de protection correspondants	Aucun 	Personnes faisant face aux zone ATEX	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	0 Accicent/Incident 
73	73	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Cette formation permettra au bénéficiaire d’être\ncapable d’acquérir les compétences théoriques et\npratiques pour une conduite sécuritaire,\ncomprendre le fonctionnement des principaux\norganes et équipements pour une conduite\nsécuritaire et réaliser en sécurité les opérations de\nmanutention prescrites, en impliquant la mise en\nœuvre du chariot élévateur à conducteur porté.	Prise de poste et vérification.\nConduite en sécurité et manœuvres.	Aucun 	Conducteurs d'engins	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	8 Accicent/Incident du à la mauvaise manipulation d'engin 
74	74	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Cette formation permettra au bénéficiaire d’être\ncapable d’acquérir les compétences théoriques et\npratiques pour une conduite sécuritaire,\ncomprendre le fonctionnement des principaux\norganes et équipements pour une conduite\nsécuritaire et réaliser en sécurité les opérations de\nmanutention prescrites, en impliquant la mise en\nœuvre du chariot élévateur à conducteur porté.	Prise de poste et vérification.\nConduite en sécurité et manœuvres.	Aucun 	Conducteurs d'engins	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	7 Accicent/Incident du à la mauvaise manipulation d'engin 
75	75	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Cette formation permettra au bénéficiaire d’être\ncapable d’acquérir les compétences théoriques et\npratiques pour une conduite sécuritaire,\ncomprendre le fonctionnement des principaux\norganes et équipements pour une conduite\nsécuritaire et réaliser en sécurité les opérations de\nmanutention prescrites, en impliquant la mise en\nœuvre du chariot élévateur à conducteur porté.	Prise de poste et vérification.\nConduite en sécurité et manœuvres.	Aucun 	Conducteurs d'engins	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	2 Accicent/Incident du à la mauvaise manipulation d'engin 
76	76	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
77	77	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Comprendre les principes fondamentaux de l’asservissement de position et de vitesse.\n► Modéliser et analyser des systèmes asservis.\n► Concevoir et implémenter des boucles d’asservissement en position et en vitesse.\n► Utiliser des outils de simulation pour valider les performances des systèmes.\n► Mettre en œuvre des systèmes d’asservissement sur des bancs didactiques.	Comprendre les principes fondamentaux de l’asservissement de position et de vitesse.\n► Modéliser et analyser des systèmes asservis.\n► Concevoir et implémenter des boucles d’asservissement en position et en vitesse.\n► Utiliser des outils de simulation pour valider les performances des systèmes.\n► Mettre en œuvre des systèmes d’asservissement sur des bancs didactiques.	\N	Mantenanciers 	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
78	78	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	A l’issu de cette formation, les bénéficiaires seront capables de :\n► Démarrer et arrêter un robot FANUC en sécurité\n► Utiliser le Teach-pendant pour naviguer et exécuter des programmes\n► Lire, interpréter et modifier un programme simple\n► Gérer les modes de fonctionnement, les alarmes, et les cycles automatiques	A l’issu de cette formation, les bénéficiaires seront capables de :\n► Démarrer et arrêter un robot FANUC en sécurité\n► Utiliser le Teach-pendant pour naviguer et exécuter des programmes\n► Lire, interpréter et modifier un programme simple\n► Gérer les modes de fonctionnement, les alarmes, et les cycles automatiques	Principes de base de l'automatisme et de la robotique 	Mantenanciers 	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
79	79	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Cette formation permettra au bénéficiaire d’être\ncapable d’acquérir les compétences théoriques et\npratiques pour une conduite sécuritaire,\ncomprendre le fonctionnement des principaux\norganes et équipements pour une conduite\nsécuritaire et réaliser en sécurité les opérations de\nmanutention prescrites, en impliquant la mise en\nœuvre du chariot élévateur à conducteur porté.	Prise de poste et vérification.\nConduite en sécurité et manœuvres.	Aucun 	Conducteurs d'engins	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	5 Accicent/Incident du à la mauvaise manipulation d'engin 
80	92	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	A l’issu de cette formation, les bénéficiaires seront capables de :\n► Lire les schémas électriques du robot et du contrôleur FANUC,\n► Diagnostiquer les principales pannes électriques,\n► Réaliser des interventions en sécurité sur les composants électriques,\n► Identifier les éléments critiques du système électrique (I/O, moteurs,\nvariateurs).	A l’issu de cette formation, les bénéficiaires seront capables de :\n► Lire les schémas électriques du robot et du contrôleur FANUC,\n► Diagnostiquer les principales pannes électriques,\n► Réaliser des interventions en sécurité sur les composants électriques,\n► Identifier les éléments critiques du système électrique (I/O, moteurs,\nvariateurs).	Principes de base de l'automatisme et de la robotique 	Mantenanciers 	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
81	80	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	À l'issue de ce e forma on, les par cipants seront capables de : 1. Comprendre le principe de fonc onnement d’un variateur de vitesse. 2. Effectuer les paramétrages de base et avancés du variateur. 3. Diagnos quer et résoudre les problèmes courants rencontrés sur les variateurs. 4. Savoir u liser les fonc onnalités avancées et les modes de commande du variateur pour des applica ons spécifiques. 5. Intégrer et configurer le variateur dans un environnement d'automa sa on industrielle.	À l'issue de ce e forma on, les par cipants seront capables de : 1. Comprendre le principe de fonc onnement d’un variateur de vitesse. 2. Effectuer les paramétrages de base et avancés du variateur. 3. Diagnos quer et résoudre les problèmes courants rencontrés sur les variateurs. 4. Savoir u liser les fonc onnalités avancées et les modes de commande du variateur pour des applica ons spécifiques. 5. Intégrer et configurer le variateur dans un environnement d'automa sa on industrielle.	Avoir des connaissances en électromécanique, et variateurs 	Techniciens de maintenance  Ingénieurs en automa sa on  Opérateurs et u lisateurs de variateurs de vitesse	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	0 Panne Technique 
82	82	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Préparer à l';habilitation électrique BR (Personnel chargé d’interventions d’entretien\net de dépannage).\nConnaître les méthodes et procédures à mettre en œuvre pour intervenir sur les\ninstallations électriques dans les meilleures conditions de sécurité et de continuité\nde service.\nPermettre à l’employeur de délivrer un titre d’habilitation électrique en adéquation\navec l’activité de l’agent.\n Effectuer une analyse préalable à l’intervention Identifier et analyser les risques\nélectriques dans un local, une armoire ou en champ libre Identifier, repérer et mettre\nen œuvre les EPI et les EPC.\nRéaliser une intervention d’entretien et de dépannage et rédiger les documents\nnécessaires.	Analyser les risques dans une situation donnée.\n Utiliser le matériel et les équipements de protection et connaître leurs limites\nd’utilisation\n Les fonctions des matériels électriques des domaines de tension BT et TBT.\n Réaliser des séquences de la mise en sécurité d’un circuit et les mesures de\nprévention.\n Réaliser une procédure de consignation et réaliser une vérification d’absence de\ntension (VAT).\n Les documents applicables dans le cadre des interventions de remplacement et de\nraccordement.\n Réaliser la procédure de remplacement et la procédure de raccordement.\n L’analyse de risques pour une situation donnée et correspondant à l’habilitation\nvisée (BR), Tout en mettant en œuvre les prescriptions propres à chaque opération\net en respectant les procédures d’intervention.\n Réaliser des opérations de consignation et déconsignation pour lui-même.\n Réaliser des opérations de dépannage, de mesurage et d’essai.\n Réaliser des opérations de connexion et de déconnexion en présence ou non de\ntension (inférieure à 500 V)	Aucun 	Electriciens	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	0 Accident/ Incident dû à une manipulation éléctrique 
85	85	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	permettre au bénéficiaire d’être capable d’intervenir\nefficacement face à une à une incendie et ainsi de mettre ses\ncompétences au profit de la santé et sécurité au travail, dans le\nrespect des procédures fixées par l’entreprise en matière de\nprévention	\nCapacité à jouer le rôle d'acteur de la\nprévention dans l'entreprise	Aucun 	\N	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	respect des standards HSE à 100%
89	89	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	À l'issue de ce e forma on, les par cipants seront capables de : \n1. Comprendre les principes fondamentaux des réseaux locaux industriels (RLI) et leur rôle dans \nl’automa sa on industrielle. \n2. Iden fier les différences et les avantages des réseaux PROFIBUS, PROFINET et ASI Bus. \n3. Concevoir, installer et configurer des réseaux industriels u lisant ces technologies. \n4. Diagnos quer et résoudre des pannes et erreurs courantes sur ces réseaux. \n5. Intégrer ces réseaux dans un environnement automa sé en u lisant des ou ls adaptés \n(commutateurs, PLC, terminaux, etc.). 	À l'issue de ce e forma on, les par cipants seront capables de : \n1. Comprendre les principes fondamentaux des réseaux locaux industriels (RLI) et leur rôle dans \nl’automa sa on industrielle. \n2. Iden fier les différences et les avantages des réseaux PROFIBUS, PROFINET et ASI Bus. \n3. Concevoir, installer et configurer des réseaux industriels u lisant ces technologies. \n4. Diagnos quer et résoudre des pannes et erreurs courantes sur ces réseaux. \n5. Intégrer ces réseaux dans un environnement automa sé en u lisant des ou ls adaptés \n(commutateurs, PLC, terminaux, etc.). 	Connaissances de base en maintenance industrielle et automatisme	Ingénieurs et techniciens en automa sa on et contrôle \n Responsables de la maintenance des réseaux industriels \n Techniciens réseau et en communica on industrielle	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
90	90	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Comprendre l'architecture d'un système automatisé ; \n Identifier les composants d’un système automatisé ; \n Connaître la structure d'un API M340 et TSX PREMIUM ; \n Comprendre le principe de fonctionnement d’un API ; \n Savoir programmer sous Unity Pro XL ; \n Utiliser Unity Pro pour piloter un automate Schneider M340 ; \n Acquérir les bases du diagnostic d’un automate programmable. 	Comprendre l'architecture d'un système automatisé ; \n Identifier les composants d’un système automatisé ; \n Connaître la structure d'un API M340 et TSX PREMIUM ; \n Comprendre le principe de fonctionnement d’un API ; \n Savoir programmer sous Unity Pro XL ; \n Utiliser Unity Pro pour piloter un automate Schneider M340 ; \n Acquérir les bases du diagnostic d’un automate programmable. 	Aucun 	\N	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
91	91	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Acquérir\nles connaissances des risques qui y sont reliés et doit être en\nmesure de les contrôler afin d’éviter les situations problématiques\nou dangereuses.	Acquérir les compétences nécessaires à la conduite d'un pont\nroulant en toute sécurité, de recycler ses connaissances et se\nperfectionner en matière de conduite en sécurité	Aucun 	Personnes conduisant les ponts roulants	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
92	93	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	Cette formation permettra au bénéficiaire d’être\ncapable d’acquérir les compétences théoriques et\npratiques pour une conduite sécuritaire,\ncomprendre le fonctionnement des principaux\norganes et équipements pour une conduite\nsécuritaire et réaliser en sécurité les opérations de\nmanutention prescrites, en impliquant la mise en\nœuvre du chariot élévateur à conducteur porté.	Prise de poste et vérification.\nConduite en sécurité et manœuvres.	Aucun 	Conducteurs d'engins	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	3 Accicent/Incident du à la mauvaise manipulation d'engin 
93	94	1	ACTIVE	2025-12-19 10:11:52.363197	\N	\N	\N	A l’issu de cette formation, les bénéficiaires seront capables de :\n► Démarrer et arrêter un robot FANUC en sécurité\n► Utiliser le Teach-pendant pour naviguer et exécuter des programmes\n► Lire, interpréter et modifier un programme simple\n► Gérer les modes de fonctionnement, les alarmes, et les cycles automatiques	A l’issu de cette formation, les bénéficiaires seront capables de :\n► Démarrer et arrêter un robot FANUC en sécurité\n► Utiliser le Teach-pendant pour naviguer et exécuter des programmes\n► Lire, interpréter et modifier un programme simple\n► Gérer les modes de fonctionnement, les alarmes, et les cycles automatiques	Principes de base de l'automatisme et de la robotique 	Mantenanciers 	\N	\N	\N	\N	\N	Formulaire d'évaluation à froid standard	\N
\.


--
-- Data for Name: formateur; Type: TABLE DATA; Schema: public; Owner: s3m_user
--

COPY public.formateur (id_formateur, nom, prenom, email, telephone, id_fournisseur, actif) FROM stdin;
1	Formateur 	Youssef	youssef@s3m.com	0610203040	4	t
3	test_nom_formateur	test_prenom_formateur	test_formateur@s3m.com	\N	12	t
\.


--
-- Data for Name: formation; Type: TABLE DATA; Schema: public; Owner: s3m_user
--

COPY public.formation (id_formation, module, type_formation, famille_formation, sous_famille, interne_externe, annee, reference_formation, prix_heure_mad, prix_jour_mad, d_heures, d_jours) FROM stdin;
54	Robot Fanuc Exploitation 	Technical	Technique 	\N	Externe	2025	REF-54	900.00	7200.00	32.00	4.00
59	Variateur SEW	Technical	Maintenance 	\N	Externe	2025	REF-59	900.00	7200.00	16.00	2.00
12	Automate siemens TIA PORTAL Niveau 2	Technical	Maintenance 	\N	Externe	2025	REF-12	\N	\N	24.00	3.00
13	Automate siemens TIA PORTAL Niveau 8	Technical	Maintenance 	\N	Externe	2025	REF-13	\N	\N	24.00	3.00
14	RECYCLAGE CACES C3	Technical	HSSE	\N	Externe	2025	REF-14	\N	\N	16.00	2.00
16	Team 2 Win	Soft skills	MANAGEMENT	\N	Interne	2025	REF-16	\N	\N	40.00	5.00
17	ADR	Technical	Maintenance 	\N	Externe	2025	REF-17	\N	\N	8.00	1.00
5	PILZ	Technical	Technique 	\N	Externe	2025	REF-5	900.00	7200.00	32.00	4.00
9	Maintenance préventive et corrective	Technical	Maintenance 	\N	Externe	2025	REF-9	900.00	7200.00	16.00	2.00
15	COMPAS	Informatique & outils SI	Informatique & outils SI	\N	Interne	2025	REF-15	0.00	\N	1.50	0.20
78	Robot Fanuc Exploitation-Pratique	Technical	Technique 	\N	Externe	2025	REF-78	900.00	7200.00	16.00	2.00
92	Robot Fanuc Electrique	Technical	Technique 	\N	Externe	2025	REF-92	900.00	7200.00	32.00	4.00
80	Variateur de vitesse Siemens	Technical	Maintenance 	\N	Externe	2025	REF-80	900.00	7200.00	16.00	2.00
79	CACES C3	Technical	HSSE	\N	Externe	2025	REF-79	750.00	6000.00	24.00	3.00
98	Java Basics	test_type2	Dev	\N	\N	2025	test_reference1	500.00	4000.00	16.00	2.00
99	Spring Boot	test_type	Deve	\N	\N	2025	test_reference2	500.00	4000.00	16.00	2.00
100	SQL Advanced	test_type	Data	\N	EXTERNE	2025	test_reference3	600.00	4800.00	16.00	2.00
103	test_module	test_type	test_famille	test_sousfamille	interne	2025	test_reference	20.00	\N	8.00	\N
49	Variateur Allain Brandley	Technical	Technique 	sous_famille_test	Externe	2025	REF-49	900.00	7200.00	16.00	2.00
52	NIDEC LEROY SOMER	Technical	Maintenance 	s	Externe	2025	REF-52	900.00	7200.00	8.00	1.00
21	CACES C3+ Dextérité C12	Technical	Maintenance 	\N	Externe	2025	REF-21	\N	\N	40.00	5.00
37	CACES C3+ Dextérité C13	Technical	Maintenance 	\N	Externe	2025	REF-37	\N	\N	40.00	5.00
53	CACES C3+ Dextérité C14	Technical	Maintenance 	\N	Externe	2025	REF-53	\N	\N	40.00	5.00
81	Automate siemens TIA PORTAL Niveau 4	Technical	Maintenance 	\N	Externe	2025	REF-81	\N	\N	24.00	3.00
2	Scrutateur	Technical	Technique 	\N	Externe	2025	REF-2	900.00	7200.00	16.00	2.00
3	CACES C3+ Dextérité C5	Technical	Maintenance 	\N	Externe	2025	REF-3	\N	\N	40.00	5.00
4	Habilitation éléctrique Basse tension 	Technical	Maintenance 	\N	Externe	2025	REF-4	\N	\N	24.00	3.00
6	CACES C3+ Dextérité C4	Technical	Maintenance 	\N	Externe	2025	REF-6	\N	\N	40.00	5.00
7	Automate siemens TIA PORTAL Niveau 9	Technical	Maintenance 	\N	Externe	2025	REF-7	\N	\N	24.00	3.00
8	Habilitation électrique B0 / BS	Safety & Security & Regulatory	HSSE	\N	Externe	2025	REF-8	\N	\N	8.00	1.00
10	Robot DURR_Booth ASU	Technical	Maintenance 	\N	Externe	2025	REF-10	\N	\N	24.00	3.00
11	ROBOT ABB 	Technical	HSSE	\N	Externe	2025	REF-11	\N	\N	24.00	3.00
19	ISO 9001 et 19001	Qualité	Qualité	\N	Externe	2025	REF-19	\N	\N	32.00	4.00
22	Pont Roulant	Technical	HSSE	\N	Externe	2025	REF-22	\N	\N	16.00	2.00
23	IRCA	Qualité	Technique 	\N	Externe	2025	REF-23	\N	\N	40.00	5.00
24	Conduite nacelle 	Technical	HSSE	\N	Externe	2025	REF-24	\N	\N	8.00	1.00
25	Automate siemens TIA PORTAL Niveau 1	Technical	Maintenance 	\N	Externe	2025	REF-25	\N	\N	24.00	3.00
26	SST	Safety & Security & Regulatory	HSSE	\N	Externe	2025	REF-26	\N	\N	8.00	1.00
27	Robot DURR_ED software Training	Technical	Maintenance 	\N	Externe	2025	REF-27	\N	\N	40.00	5.00
28	Habilitation Electrique HT	Technical	Technique 	\N	Externe	2025	REF-28	\N	\N	40.00	5.00
29	Conduite nacelle et travaux en hauteur 	Safety & Security & Regulatory	HSSE	\N	Externe	2025	REF-29	\N	\N	16.00	2.00
18	Variateur de vitesse Siemens	Technical	Maintenance 	\N	Externe	2025	REF-18	900.00	7200.00	16.00	2.00
20	Robot Fanuc Mécanique 	Technical	Technique 	\N	Externe	2025	REF-20	900.00	7200.00	16.00	2.00
30	Communication et bus de terrain (Asi, Profibus, Profinet)	Technical	Maintenance 	\N	Externe	2025	REF-30	900.00	7200.00	32.00	4.00
32	Formation Maintenance Electrique Robot ABB	Techincal	Programmation	\N	Externe	2025	REF-32	\N	\N	32.00	4.00
33	CACES C3+ Dextérité C6	Technical	Maintenance 	\N	Externe	2025	REF-33	\N	\N	40.00	5.00
34	CACES C3+ Dextérité C3	Technical	Maintenance 	\N	Externe	2025	REF-34	\N	\N	40.00	5.00
35	Habilitation Electrique B2TL	Technical	Maintenance 	\N	Externe	2025	REF-35	\N	\N	16.00	2.00
36	Sensibilisation à l'environnement 	Safety & Security & Regulatory	HSSE	\N	Externe	2025	REF-36	\N	\N	8.00	1.00
38	Formation IT 	Informatique & outils SI	Informatique & outils SI	\N	Interne	2025	REF-38	\N	\N	8.00	1.00
39	CACES C3+ Dextérité C8	Technical	Maintenance 	\N	Externe	2025	REF-39	\N	\N	40.00	5.00
40	SST et EPI	Safety & Security & Regulatory	HSSE	\N	Externe	2025	REF-40	\N	\N	16.00	2.00
41	Habilitation distribution de gaz 	Safety & Security & Regulatory	HSSE	\N	Externe	2025	REF-41	\N	\N	16.00	2.00
42	CACES C3+ Dextérité C11	Technical	Maintenance 	\N	Externe	2025	REF-42	\N	\N	40.00	5.00
43	API SHNEIDER Niveau 1 	Technical	Maintenance 	\N	Externe	2025	REF-43	\N	\N	8.00	1.00
44	Automate siemens TIA PORTAL Niveau 3	Technical	Maintenance 	\N	Externe	2025	REF-44	\N	\N	24.00	3.00
45	TIA Portal Niv. 1	Technical	Maintenance 	\N	Externe	2025	REF-45	\N	\N	24.00	3.00
47	SST	Safety & Security & Regulatory	HSSE	\N	Externe	2025	REF-47	\N	\N	8.00	1.00
31	Communication et bus de terrain (Asi, Profibus, Profinet)	Technical	Maintenance 	\N	Externe	2025	REF-31	900.00	7200.00	32.00	4.00
46	Variateur de vitesse telemécanique shneider	Technical	Maintenance 	\N	Externe	2025	REF-46	900.00	7200.00	16.00	2.00
51	Robot DURR	Technical	Maintenance 	\N	Externe	2025	REF-51	\N	\N	24.00	3.00
55	Habilitation batterie	Technical	Technique 	\N	Externe	2025	REF-55	\N	\N	24.00	3.00
56	RECYCLAGE CACES C2	Technical	HSSE	\N	Externe	2025	REF-56	\N	\N	16.00	2.00
58	Automate siemens TIA PORTAL Niveau 6	Technical	Maintenance 	\N	Externe	2025	REF-58	\N	\N	24.00	3.00
61	Robot DURR_OVEN Software	Technical	Maintenance 	\N	Externe	2025	REF-61	\N	\N	32.00	4.00
62	Automate siemens TIA PORTAL Niveau 7	Technical	Maintenance 	\N	Externe	2025	REF-62	\N	\N	24.00	3.00
63	Formation IT 	Informatique & outils SI	Informatique & outils SI	\N	Interne	2025	REF-63	\N	\N	8.00	1.00
48	CACES C3	Technical	HSSE	\N	Externe	2025	REF-48	750.00	6000.00	24.00	3.00
50	CACES C3	Technical	HSSE	\N	Externe	2025	REF-50	750.00	6000.00	24.00	3.00
57	CACES C3	Technical	HSSE	\N	Externe	2025	REF-57	750.00	6000.00	24.00	3.00
60	CACES C3	Technical	HSSE	\N	Externe	2025	REF-60	750.00	6000.00	24.00	3.00
65	Habilitation Electrique B2TL	Technical	Maintenance 	\N	Externe	2025	REF-65	\N	\N	16.00	2.00
66	MHF - Sicim pour l’utilisateur et le codificateur 	Technical	Maintenance 	\N	Externe	2025	REF-66	\N	\N	8.00	1.00
67	Habilitation AW18	Technical	HSSE	\N	Externe	2025	REF-67	\N	\N	24.00	3.00
68	Robotique DURR 	Technical	Maintenance 	\N	Externe	2025	REF-68	\N	\N	8.00	1.00
69	Formation IT 	Informatique & outils SI	Informatique & outils SI	\N	Interne	2025	REF-69	\N	\N	8.00	1.00
71	ROBOT ABB 	Technical	HSSE	\N	Externe	2025	REF-71	\N	\N	24.00	3.00
72	ATEX	Technical	HSSE	\N	Externe	2025	REF-72	\N	\N	16.00	2.00
76	Risques chimiques 	Safety & Security & Regulatory	HSSE	\N	Externe	2025	REF-76	\N	\N	8.00	\N
70	Variateur SEW	Technical	Maintenance 	\N	Externe	2025	REF-70	900.00	7200.00	16.00	2.00
77	Asservissement de positionnement de vitesse 	Technical	Technique 	\N	Externe	2025	REF-77	900.00	7200.00	32.00	4.00
64	CACES C3	Technical	HSSE	\N	Externe	2025	REF-64	750.00	6000.00	24.00	3.00
73	CACES C3	Technical	HSSE	\N	Externe	2025	REF-73	750.00	6000.00	24.00	3.00
74	CACES C3	Technical	HSSE	\N	Externe	2025	REF-74	750.00	6000.00	24.00	3.00
75	CACES C3	Technical	HSSE	\N	Externe	2025	REF-75	750.00	6000.00	24.00	3.00
82	Habilitation éléctrique Basse tension 	Technical	Maintenance 	\N	Externe	2025	REF-82	\N	\N	24.00	3.00
83	CACES C3+ Dextérité C9	Technical	Maintenance 	\N	Externe	2025	REF-83	\N	\N	40.00	5.00
84	Automate siemens TIA PORTAL Niveau 5	Technical	Maintenance 	\N	Externe	2025	REF-84	\N	\N	24.00	3.00
85	EPI	Safety & Security & Regulatory	HSSE	\N	Externe	2025	REF-85	\N	\N	8.00	1.00
86	Automate siemens TIA PORTAL Niveau 10	Technical	Maintenance 	\N	Externe	2025	REF-86	\N	\N	24.00	3.00
87	CACES C3+ Dextérité C10	Technical	Maintenance 	\N	Externe	2025	REF-87	\N	\N	40.00	5.00
88	CACES C3+ Dextérité C7	Technical	Maintenance 	\N	Externe	2025	REF-88	\N	\N	40.00	5.00
90	Habilitation AW18	Technical	HSSE	\N	Externe	2025	REF-90	\N	\N	24.00	3.00
91	Ponts Roulants	Technical	Technique 	\N	Externe	2025	REF-91	\N	\N	16.00	2.00
89	Communication et bus de terrain (Asi, Profibus, Profinet)	Technical	Maintenance 	\N	Externe	2025	REF-89	900.00	7200.00	32.00	4.00
94	Robot Fanuc Exploitation	Technical	Technique 	\N	Externe	2025	REF-94	900.00	7200.00	16.00	2.00
93	CACES C3	Technical	HSSE	\N	Externe	2025	REF-93	750.00	6000.00	24.00	3.00
\.


--
-- Data for Name: participation; Type: TABLE DATA; Schema: public; Owner: s3m_user
--

COPY public.participation (id_participation, id_session, id_employe) FROM stdin;
510	59	6
511	64	6
512	68	6
513	133	6
514	167	6
515	225	6
516	11	8
517	134	8
518	168	8
519	58	10
520	63	10
521	224	10
522	11	16
523	2	19
524	10	19
525	40	19
526	61	19
527	66	19
528	201	19
529	212	19
530	227	19
531	201	20
532	30	25
533	34	25
534	10	33
535	29	33
536	30	33
537	34	33
538	34	34
539	2	35
540	41	35
541	213	35
542	32	39
543	61	39
544	66	39
545	117	39
546	118	39
547	227	39
548	68	40
550	35	45
551	132	48
552	166	48
553	32	50
554	41	50
555	44	50
556	45	50
557	61	50
558	66	50
559	117	50
560	118	50
561	120	50
562	201	50
563	213	50
564	227	50
565	234	50
566	30	55
567	40	58
568	212	58
569	95	64
570	132	69
571	166	69
572	197	69
573	59	77
574	64	77
575	225	77
576	134	79
577	168	79
578	41	82
579	213	82
580	34	83
581	36	89
582	60	90
583	65	90
584	117	90
585	118	90
586	197	90
587	226	90
588	132	91
589	166	91
590	199	91
591	132	92
592	166	92
593	35	102
594	62	103
595	67	103
596	228	103
597	132	104
598	166	104
599	29	107
600	39	107
601	211	107
602	35	108
603	3	109
604	2	115
605	39	115
606	117	115
607	118	115
608	211	115
609	39	118
610	211	118
611	4	125
612	32	125
613	60	125
614	65	125
615	95	125
616	200	125
617	226	125
618	68	127
619	36	128
620	60	129
621	65	129
622	226	129
623	5	130
624	134	130
625	168	130
626	134	133
627	168	133
628	59	134
629	64	134
630	225	134
631	35	150
632	32	151
633	62	151
634	67	151
635	117	151
636	118	151
637	197	151
638	228	151
639	33	152
640	44	159
641	45	159
642	58	159
643	62	159
644	63	159
645	67	159
646	120	159
647	224	159
648	228	159
649	234	159
650	59	162
651	64	162
652	225	162
653	5	164
654	58	164
655	63	164
656	134	164
657	168	164
658	224	164
659	197	167
660	30	172
662	132	177
663	166	177
664	95	183
665	58	185
666	63	185
667	224	185
668	39	186
669	133	186
670	167	186
671	211	186
672	59	192
673	64	192
674	132	192
675	166	192
676	225	192
677	41	197
678	60	197
679	65	197
680	107	197
681	117	197
682	118	197
683	197	197
684	213	197
685	226	197
686	36	198
687	44	200
688	45	200
689	120	200
690	234	200
691	117	202
692	118	202
693	2	207
694	10	207
695	40	207
696	61	207
697	66	207
698	201	207
699	212	207
700	227	207
701	133	211
702	167	211
703	59	212
704	64	212
705	225	212
706	33	213
707	35	217
708	43	219
709	132	219
710	166	219
711	202	219
712	233	219
713	243	219
714	36	220
715	33	224
716	43	228
717	202	228
718	233	228
719	243	228
720	30	235
721	60	235
722	65	235
723	226	235
724	32	239
725	40	239
726	44	239
727	45	239
728	60	239
729	65	239
730	107	239
731	120	239
732	199	239
733	201	239
734	212	239
735	226	239
736	234	239
737	22	243
738	36	247
739	107	250
740	4	251
741	133	253
742	167	253
743	62	263
744	67	263
745	199	263
746	228	263
747	95	264
748	133	264
749	167	264
750	29	270
751	60	270
752	65	270
753	226	270
754	30	274
755	44	274
756	45	274
757	60	274
758	65	274
759	95	274
760	107	274
761	120	274
762	226	274
763	234	274
764	36	275
765	35	276
766	29	277
767	41	277
768	213	277
769	36	278
770	33	281
771	36	282
772	35	287
773	4	289
774	134	289
775	168	289
776	3	295
777	132	295
778	166	295
779	199	295
780	31	302
781	107	302
782	33	304
783	11	313
784	58	313
785	63	313
786	224	313
787	60	315
788	65	315
789	226	315
790	60	331
791	65	331
792	226	331
793	35	333
794	59	336
795	64	336
796	225	336
797	68	353
798	39	361
799	43	361
800	59	361
801	64	361
802	133	361
803	167	361
804	202	361
805	211	361
806	225	361
807	233	361
808	243	361
809	36	366
810	30	367
811	36	373
812	35	384
813	35	389
814	33	392
815	4	396
816	41	396
817	44	396
818	45	396
819	62	396
820	67	396
821	120	396
822	213	396
823	228	396
824	234	396
825	36	401
826	35	403
827	31	404
828	34	404
829	35	408
830	35	410
831	2	411
832	29	411
833	39	411
834	62	411
835	67	411
836	133	411
837	167	411
838	211	411
839	228	411
840	30	416
841	34	416
842	44	416
843	45	416
844	59	416
845	64	416
846	120	416
847	225	416
848	234	416
849	197	420
850	35	429
851	41	430
852	213	430
853	60	435
854	65	435
855	226	435
856	61	437
857	66	437
858	227	437
859	60	442
860	65	442
861	198	442
862	226	442
863	59	447
864	64	447
865	225	447
866	36	451
867	41	456
868	213	456
869	5	457
870	134	457
871	168	457
872	33	466
873	22	467
875	133	487
876	167	487
877	44	492
878	45	492
879	120	492
880	234	492
881	4	505
882	29	505
883	39	505
884	44	505
885	45	505
886	120	505
887	133	505
888	167	505
889	211	505
890	234	505
891	61	507
892	66	507
893	227	507
894	33	508
895	11	510
896	22	514
897	22	570
898	36	571
899	3	593
900	35	596
901	2	602
902	41	602
903	213	602
904	33	604
905	29	612
906	34	616
907	43	620
908	202	620
909	233	620
910	243	620
911	3	622
912	39	622
913	41	622
914	199	622
915	211	622
916	213	622
917	30	634
918	134	646
919	168	646
920	39	652
921	68	652
922	211	652
923	22	654
924	35	656
925	10	657
926	36	669
927	43	672
928	61	672
929	66	672
930	202	672
931	227	672
932	233	672
933	243	672
934	36	678
935	36	679
936	30	684
937	34	684
938	35	693
939	36	699
940	58	704
941	63	704
942	132	704
943	166	704
944	197	704
945	224	704
946	36	718
947	35	720
948	30	739
949	34	739
950	59	745
951	64	745
952	225	745
953	35	759
954	2	767
955	41	773
956	213	773
957	197	774
958	58	778
959	63	778
960	224	778
961	3	779
962	35	781
963	132	784
964	166	784
965	132	789
966	166	789
967	35	792
968	36	802
969	58	814
970	63	814
971	107	814
972	197	814
973	224	814
974	29	816
975	133	819
976	167	819
977	36	824
978	36	844
979	35	856
980	36	860
981	36	880
982	36	881
983	5	884
984	35	885
985	36	888
986	22	897
987	61	902
988	66	902
989	117	902
990	118	902
991	227	902
992	35	916
993	107	922
994	36	936
995	39	942
996	211	942
997	35	955
998	2	957
999	31	972
1000	36	979
1001	134	995
1002	168	995
1003	35	1002
1004	199	1003
1005	11	1007
1006	40	1007
1007	212	1007
1008	34	1025
1009	43	1039
1010	202	1039
1011	233	1039
1012	243	1039
1013	43	1065
1014	202	1065
1015	233	1065
1016	243	1065
1017	35	1067
1018	35	1091
1019	249	1110
1020	250	1109
1021	269	2
1028	285	1110
1029	285	2
1030	285	417
1032	286	1110
1033	286	2
1034	287	1110
1035	287	416
1036	4	2
1037	251	1110
1040	269	1110
1044	289	2
1045	289	416
1046	289	418
1047	272	2
1053	290	421
1055	291	416
1056	292	2
1057	4	416
1059	4	486
1060	4	228
1063	235	2
1066	279	2
\.


--
-- Data for Name: session_formation; Type: TABLE DATA; Schema: public; Owner: s3m_user
--

COPY public.session_formation (id_session, id_formation, id_entreprise, date_debut, date_fin, d_heures, d_jours, formateur, fournisseur, id_formateur, id_fournisseur, statut, id_demande, reference_session) FROM stdin;
4	2	1	2025-09-23	2025-09-24	16.00	2.00	Jamal Belmeknassi	S3M	\N	4	\N	\N	\N
5	2	1	2025-09-25	2025-09-26	16.00	2.00	Jamal Belmeknassi	S3M	\N	4	\N	\N	\N
234	92	1	2025-10-24	2025-10-27	32.00	4.00	Jamal Belmeknassi	S3M	\N	4	\N	\N	\N
235	93	1	2025-02-03	2025-02-05	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
236	93	1	2025-02-19	2025-02-21	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
237	93	1	2025-02-26	2025-02-28	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
238	93	1	2025-04-21	2025-04-23	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
31	15	1	2025-07-08	2025-07-08	1.50	0.20	ANAS MOHAMMADINE 	STELLANTIS	\N	1	\N	\N	\N
32	15	1	2025-09-09	2025-09-09	1.50	0.20	ANAS MOHAMMADINE 	STELLANTIS	\N	1	\N	\N	\N
33	15	1	2025-10-09	2025-10-09	1.50	0.20	ANAS MOHAMMADINE 	STELLANTIS	\N	1	\N	\N	\N
34	15	1	2025-10-23	2025-10-23	1.50	0.20	ANAS MOHAMMADINE 	STELLANTIS	\N	1	\N	\N	\N
35	16	1	2025-11-10	2025-11-14	40.00	5.00	Formateurs Interne	Stellantis	\N	2	\N	\N	\N
36	16	1	2025-11-17	2025-11-21	40.00	5.00	Formateurs Interne	Stellantis	\N	2	\N	\N	\N
37	17	1	2025-10-27	2025-10-27	8.00	1.00	\N	IFMIA	\N	8	\N	\N	\N
38	17	1	2025-10-27	2025-10-27	8.00	\N	\N	IFMIA	\N	8	\N	\N	\N
39	18	1	2025-03-25	2025-03-26	16.00	2.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
40	18	1	2025-07-03	2025-07-04	16.00	2.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
41	18	1	2025-07-15	2025-07-16	16.00	2.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
42	19	1	2025-07-01	2025-07-04	32.00	4.00	\N	IFMIA	\N	8	\N	\N	\N
43	20	1	2025-07-30	2025-08-02	16.00	2.00	Jamal Belmeknassi	S3M	\N	4	\N	\N	\N
44	20	1	2025-10-02	2025-10-03	32.00	4.00	Jamal Belmeknassi	S3M	\N	4	\N	\N	\N
45	20	1	2025-10-02	2025-10-06	32.00	4.00	Jamal Belmeknassi	S3M	\N	4	\N	\N	\N
46	21	1	2025-10-20	2025-10-24	40.00	5.00	\N	IFMIA	\N	8	\N	\N	\N
47	22	1	2025-02-24	2025-02-25	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
48	23	1	2025-07-14	2025-07-18	40.00	5.00	\N	SGS	\N	6	\N	\N	\N
49	24	1	2025-04-10	2025-04-10	8.00	1.00	\N	IFMIA	\N	8	\N	\N	\N
50	24	1	2025-09-18	2025-09-19	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
51	25	1	2025-10-16	2025-10-17	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
52	26	1	2025-03-07	2025-03-07	8.00	1.00	\N	IFMIA	\N	8	\N	\N	\N
53	26	1	2025-05-22	2025-05-22	8.00	1.00	\N	IFMIA	\N	8	\N	\N	\N
54	26	1	2025-10-09	2025-10-09	8.00	1.00	\N	IFMIA	\N	8	\N	\N	\N
55	27	1	2025-10-20	2025-10-24	40.00	5.00	\N	DURR	\N	10	\N	\N	\N
56	28	1	2025-07-21	2025-07-29	40.00	5.00	\N	ONE	\N	5	\N	\N	\N
57	29	1	2025-05-02	2025-05-03	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
58	30	1	2025-01-01	2025-01-04	32.00	4.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
59	30	1	2025-02-25	2025-03-01	32.00	4.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
60	30	1	2025-07-29	2025-08-01	32.00	4.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
61	30	1	2025-08-26	2025-08-29	32.00	4.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
62	30	1	2025-09-02	2025-09-05	32.00	4.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
63	31	1	2025-01-01	2025-01-04	32.00	4.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
64	31	1	2025-02-25	2025-03-01	32.00	4.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
65	31	1	2025-07-29	2025-08-01	32.00	4.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
66	31	1	2025-08-26	2025-08-29	32.00	4.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
67	31	1	2025-09-02	2025-09-05	32.00	4.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
68	32	1	2025-11-24	2025-11-27	32.00	4.00	Abdelali Saddam	IFMIA	\N	8	\N	\N	\N
69	33	1	2025-10-20	2025-10-24	40.00	5.00	\N	IFMIA	\N	8	\N	\N	\N
70	34	1	2025-10-20	2025-10-24	40.00	5.00	\N	IFMIA	\N	8	\N	\N	\N
71	35	1	2025-09-11	2025-09-12	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
72	35	1	2025-10-30	2025-10-31	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
73	36	1	2025-09-23	2025-09-23	8.00	1.00	\N	IFMIA	\N	8	\N	\N	\N
74	36	1	2025-09-30	2025-09-30	8.00	1.00	\N	IFMIA	\N	8	\N	\N	\N
75	36	1	2025-10-07	2025-10-07	8.00	1.00	\N	IFMIA	\N	8	\N	\N	\N
76	36	1	2025-10-14	2025-10-14	8.00	1.00	\N	IFMIA	\N	8	\N	\N	\N
77	36	1	2025-10-21	2025-10-21	8.00	1.00	\N	IFMIA	\N	8	\N	\N	\N
78	36	1	2025-10-28	2025-10-28	8.00	1.00	\N	IFMIA	\N	8	\N	\N	\N
79	37	1	2025-10-20	2025-10-24	40.00	5.00	\N	IFMIA	\N	8	\N	\N	\N
80	38	1	2025-05-26	2025-05-26	8.00	1.00	\N	STELLANTIS	\N	1	\N	\N	\N
81	38	1	2025-10-09	2025-10-09	8.00	1.00	\N	STELLANTIS	\N	1	\N	\N	\N
82	38	1	2025-10-29	2025-10-29	8.00	1.00	\N	STELLANTIS	\N	1	\N	\N	\N
83	39	1	2025-10-20	2025-10-24	40.00	5.00	\N	IFMIA	\N	8	\N	\N	\N
84	40	1	2025-09-25	2025-09-26	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
85	40	1	2025-10-23	2025-10-24	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
86	41	1	2025-09-23	2025-09-24	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
87	41	1	2025-09-29	2025-09-30	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
88	41	1	2025-10-07	2025-10-08	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
89	42	1	2025-10-20	2025-10-24	40.00	5.00	\N	IFMIA	\N	8	\N	\N	\N
90	43	1	2025-04-23	2025-04-24	8.00	1.00	\N	IFMIA	\N	8	\N	\N	\N
91	43	1	2025-04-23	2025-04-24	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
92	43	1	2025-05-07	2025-05-09	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
93	44	1	2025-10-16	2025-10-17	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
94	45	1	2025-05-15	2025-05-16	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
95	46	1	2025-07-01	2025-07-02	16.00	2.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
96	47	1	2025-03-07	2025-03-07	8.00	1.00	\N	IFMIA	\N	8	\N	\N	\N
97	47	1	2025-05-22	2025-05-22	8.00	1.00	\N	IFMIA	\N	8	\N	\N	\N
98	47	1	2025-10-09	2025-10-09	8.00	1.00	\N	IFMIA	\N	8	\N	\N	\N
99	48	1	2025-02-03	2025-02-05	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
100	48	1	2025-02-19	2025-02-21	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
101	48	1	2025-02-26	2025-02-28	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
102	48	1	2025-04-21	2025-04-23	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
103	48	1	2025-04-21	2025-04-23	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
104	48	1	2025-05-07	2025-05-09	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
105	48	1	2025-07-14	2025-07-16	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
106	48	1	2025-09-23	2025-09-24	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
107	49	1	2025-08-08	2025-08-09	16.00	2.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
108	50	1	2025-02-03	2025-02-05	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
109	50	1	2025-02-19	2025-02-21	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
110	50	1	2025-02-26	2025-02-28	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
111	50	1	2025-04-21	2025-04-23	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
112	50	1	2025-04-21	2025-04-23	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
113	50	1	2025-05-07	2025-05-09	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
114	50	1	2025-07-14	2025-07-16	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
115	50	1	2025-09-23	2025-09-24	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
116	51	1	2025-10-27	2025-10-29	24.00	3.00	\N	DURR	\N	10	\N	\N	\N
117	52	1	2025-08-04	2025-08-05	8.00	1.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
118	52	1	2025-08-04	2025-08-05	16.00	2.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
119	53	1	2025-10-20	2025-10-24	40.00	5.00	\N	IFMIA	\N	8	\N	\N	\N
278	49	3	2026-01-09	2026-01-31	16.00	2.00	\N	\N	1	3	PLANIFIEE	\N	VAR-5050
120	54	1	2025-09-26	2025-09-29	32.00	4.00	Jamal Belmeknassi	S3M	\N	4	\N	\N	\N
121	55	1	2025-07-23	2025-07-25	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
122	56	1	2025-10-08	2025-10-09	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
123	57	1	2025-02-03	2025-02-05	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
124	57	1	2025-02-19	2025-02-21	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
125	57	1	2025-02-26	2025-02-28	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
126	57	1	2025-04-21	2025-04-23	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
127	57	1	2025-04-21	2025-04-23	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
128	57	1	2025-05-07	2025-05-09	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
129	57	1	2025-07-14	2025-07-16	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
130	57	1	2025-09-23	2025-09-24	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
131	58	1	2025-10-16	2025-10-17	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
132	59	1	2025-01-15	2025-01-16	16.00	2.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
133	59	1	2025-03-28	2025-03-29	16.00	2.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
134	59	1	2025-10-01	2025-10-02	16.00	2.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
135	60	1	2025-02-03	2025-02-05	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
136	60	1	2025-02-19	2025-02-21	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
137	60	1	2025-02-26	2025-02-28	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
138	60	1	2025-04-21	2025-04-23	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
139	60	1	2025-04-21	2025-04-23	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
140	60	1	2025-05-07	2025-05-09	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
141	60	1	2025-07-14	2025-07-16	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
142	60	1	2025-09-23	2025-09-24	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
143	61	1	2025-10-13	2025-10-16	32.00	4.00	\N	DURR	\N	10	\N	\N	\N
144	62	1	2025-10-16	2025-10-17	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
145	63	1	2025-05-26	2025-05-26	8.00	1.00	\N	STELLANTIS	\N	1	\N	\N	\N
146	63	1	2025-10-09	2025-10-09	8.00	1.00	\N	STELLANTIS	\N	1	\N	\N	\N
147	63	1	2025-10-29	2025-10-29	8.00	1.00	\N	STELLANTIS	\N	1	\N	\N	\N
148	64	1	2025-02-03	2025-02-05	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
149	64	1	2025-02-19	2025-02-21	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
150	64	1	2025-02-26	2025-02-28	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
151	64	1	2025-04-21	2025-04-23	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
152	64	1	2025-04-21	2025-04-23	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
153	64	1	2025-05-07	2025-05-09	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
154	64	1	2025-07-14	2025-07-16	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
155	64	1	2025-09-23	2025-09-24	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
156	65	1	2025-09-11	2025-09-12	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
157	65	1	2025-10-30	2025-10-31	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
158	66	1	2025-10-30	2025-10-30	8.00	1.00	\N	STELLANTIS	\N	1	\N	\N	\N
159	67	1	2025-05-06	2025-05-08	24.00	3.00	\N	AFTL	\N	3	\N	\N	\N
160	67	1	2025-05-12	2025-05-14	24.00	3.00	\N	AFTL	\N	3	\N	\N	\N
161	68	1	2025-08-28	2025-08-28	8.00	1.00	\N	DURR	\N	10	\N	\N	\N
162	68	1	2025-09-03	2025-09-03	8.00	1.00	\N	DURR	\N	10	\N	\N	\N
163	69	1	2025-05-26	2025-05-26	8.00	1.00	\N	STELLANTIS	\N	1	\N	\N	\N
164	69	1	2025-10-09	2025-10-09	8.00	1.00	\N	STELLANTIS	\N	1	\N	\N	\N
165	69	1	2025-10-29	2025-10-29	8.00	1.00	\N	STELLANTIS	\N	1	\N	\N	\N
166	70	1	2025-01-15	2025-01-16	16.00	2.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
167	70	1	2025-03-28	2025-03-29	16.00	2.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
168	70	1	2025-10-01	2025-10-02	16.00	2.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
169	71	1	2025-05-14	2025-05-16	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
170	71	1	2025-05-21	2025-05-23	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
171	72	1	2025-02-25	2025-02-26	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
172	73	1	2025-02-03	2025-02-05	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
173	73	1	2025-02-19	2025-02-21	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
174	73	1	2025-02-26	2025-02-28	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
175	73	1	2025-04-21	2025-04-23	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
176	73	1	2025-04-21	2025-04-23	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
177	73	1	2025-05-07	2025-05-09	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
178	73	1	2025-07-14	2025-07-16	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
179	73	1	2025-09-23	2025-09-24	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
180	74	1	2025-02-03	2025-02-05	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
181	74	1	2025-02-19	2025-02-21	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
182	74	1	2025-02-26	2025-02-28	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
183	74	1	2025-04-21	2025-04-23	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
184	74	1	2025-04-21	2025-04-23	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
185	74	1	2025-05-07	2025-05-09	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
186	74	1	2025-07-14	2025-07-16	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
187	74	1	2025-09-23	2025-09-24	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
188	75	1	2025-02-03	2025-02-05	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
189	75	1	2025-02-19	2025-02-21	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
190	75	1	2025-02-26	2025-02-28	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
191	75	1	2025-04-21	2025-04-23	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
192	75	1	2025-04-21	2025-04-23	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
193	75	1	2025-05-07	2025-05-09	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
194	75	1	2025-07-14	2025-07-16	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
195	75	1	2025-09-23	2025-09-24	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
196	76	1	2025-10-24	2025-10-24	8.00	\N	\N	IFMIA	\N	8	\N	\N	\N
197	77	1	2025-08-12	2025-08-15	32.00	4.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
198	77	1	2025-08-19	2025-08-19	8.00	1.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
199	77	1	2025-08-19	2025-08-22	32.00	4.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
200	77	1	2025-08-20	2025-08-22	24.00	3.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
201	77	1	2025-09-16	2025-09-19	32.00	4.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
202	78	1	2025-07-28	2025-07-29	16.00	2.00	Jamal Belmeknassi	S3M	\N	4	\N	\N	\N
203	79	1	2025-02-03	2025-02-05	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
204	79	1	2025-02-19	2025-02-21	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
205	79	1	2025-02-26	2025-02-28	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
206	79	1	2025-04-21	2025-04-23	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
207	79	1	2025-04-21	2025-04-23	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
208	79	1	2025-05-07	2025-05-09	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
209	79	1	2025-07-14	2025-07-16	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
210	79	1	2025-09-23	2025-09-24	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
211	80	1	2025-03-25	2025-03-26	16.00	2.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
239	93	1	2025-04-21	2025-04-23	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
240	93	1	2025-05-07	2025-05-09	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
241	93	1	2025-07-14	2025-07-16	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
242	93	1	2025-09-23	2025-09-24	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
243	94	1	2025-07-22	2025-07-23	16.00	2.00	Jamal Belmeknassi	S3M	\N	4	\N	\N	\N
3	2	1	2025-03-01	2025-03-06	16.00	2.00	Jamal Belmeknassi	S3M	1	1	TERMINEE	5	\N
2	2	1	2025-07-07	2025-07-08	16.00	2.00	Jamal Belmeknassi	S3M	1	1	EN_COURS	4	\N
250	99	1	2025-12-05	2025-12-06	16.00	2.00	\N	\N	3	12	TERMINEE	7	\N
269	2	1	2026-02-01	2026-02-05	16.00	2.00	\N	\N	1	2	PLANIFIEE	\N	SCR-2347
274	98	2	2026-01-01	2026-01-01	2.00	0.25	\N	\N	3	4	PLANIFIEE	\N	JAV-7151
279	49	1	2026-02-02	2026-02-01	16.00	2.00	\N	\N	1	3	PLANIFIEE	\N	VAR-9293
212	80	1	2025-07-03	2025-07-04	16.00	2.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
213	80	1	2025-07-15	2025-07-16	16.00	2.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
214	81	1	2025-10-16	2025-10-17	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
215	82	1	2025-01-29	2025-01-31	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
216	82	1	2025-04-28	2025-04-30	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
217	82	1	2025-10-27	2025-10-29	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
218	83	1	2025-10-20	2025-10-24	40.00	5.00	\N	IFMIA	\N	8	\N	\N	\N
280	100	1	2026-02-13	2026-02-14	16.20	2.03	\N	\N	1	4	PLANIFIEE	\N	SQL-8623
284	49	2	2026-02-01	2026-02-02	16.40	2.00	\N	\N	1	4	PLANIFIEE	\N	VAR-1058
268	52	3	2026-02-01	2026-02-05	8.00	1.00	\N	\N	1	3	TERMINEE	\N	SCR-8620
290	54	1	2026-02-20	2026-02-27	32.00	4.00	\N	\N	1	4	PLANIFIEE	\N	ROB-3836
219	84	1	2025-10-16	2025-10-17	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
220	85	1	2025-10-10	2025-10-10	8.00	1.00	\N	IFMIA	\N	8	\N	\N	\N
221	86	1	2025-10-16	2025-10-17	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
222	87	1	2025-10-20	2025-10-24	40.00	5.00	\N	IFMIA	\N	8	\N	\N	\N
223	88	1	2025-10-20	2025-10-24	40.00	5.00	\N	IFMIA	\N	8	\N	\N	\N
224	89	1	2025-01-01	2025-01-04	32.00	4.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
6	3	1	2025-10-20	2025-10-24	40.00	5.00	\N	IFMIA	\N	8	\N	\N	\N
7	4	1	2025-01-29	2025-01-31	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
8	4	1	2025-04-28	2025-04-30	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
9	4	1	2025-10-27	2025-10-29	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
10	5	1	2025-07-08	2025-07-11	32.00	4.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
11	5	1	2025-10-11	2025-10-18	32.00	4.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
12	6	1	2025-10-20	2025-10-24	40.00	5.00	\N	IFMIA	\N	8	\N	\N	\N
13	7	1	2025-10-16	2025-10-17	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
14	8	1	2025-04-17	2025-04-19	8.00	1.00	\N	IFMIA	\N	8	\N	\N	\N
15	8	1	2025-04-17	2025-04-19	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
16	8	1	2025-04-17	2025-04-19	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
17	8	1	2025-06-30	2025-07-04	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
18	8	1	2025-06-30	2025-07-04	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
19	8	1	2025-07-01	2025-07-02	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
20	8	1	2025-07-10	2025-07-14	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
21	8	1	2025-09-17	2025-09-17	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
22	9	1	2025-01-02	2025-01-03	16.00	2.00	Mohammed Kadiri	S3M	\N	4	\N	\N	\N
23	10	1	2025-10-07	2025-10-09	24.00	3.00	\N	DURR	\N	10	\N	\N	\N
24	11	1	2025-05-14	2025-05-16	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
25	11	1	2025-05-21	2025-05-23	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
26	12	1	2025-10-16	2025-10-17	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
27	13	1	2025-10-16	2025-10-17	24.00	3.00	\N	IFMIA	\N	8	\N	\N	\N
28	14	1	2025-10-06	2025-10-07	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
29	15	1	2025-04-17	2025-04-17	1.50	0.20	ANAS MOHAMMADINE 	STELLANTIS	\N	1	\N	\N	\N
30	15	1	2025-04-24	2025-04-24	1.50	0.20	ANAS MOHAMMADINE 	STELLANTIS	\N	1	\N	\N	\N
225	89	1	2025-02-25	2025-03-01	32.00	4.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
226	89	1	2025-07-29	2025-08-01	32.00	4.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
227	89	1	2025-08-26	2025-08-29	32.00	4.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
228	89	1	2025-09-02	2025-09-05	32.00	4.00	Ali Lahlali	S3M	\N	4	\N	\N	\N
229	90	1	2025-05-06	2025-05-08	24.00	3.00	\N	AFTL	\N	3	\N	\N	\N
230	90	1	2025-05-12	2025-05-14	24.00	3.00	\N	AFTL	\N	3	\N	\N	\N
231	91	1	2025-07-24	2025-07-25	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
232	91	1	2025-10-15	2025-10-16	16.00	2.00	\N	IFMIA	\N	8	\N	\N	\N
233	92	1	2025-08-13	2025-08-16	32.00	4.00	Jamal Belmeknassi	S3M	\N	4	\N	\N	\N
270	49	2	2026-01-30	2026-02-06	16.00	2.00	\N	\N	1	4	PLANIFIEE	\N	VAR-2731
275	103	2	2026-01-01	2026-01-01	8.00	1.00	\N	\N	3	\N	PLANIFIEE	\N	TES-4964
281	99	2	2026-02-03	2026-02-04	16.30	2.00	\N	\N	1	4	PLANIFIEE	\N	SPR-7682
285	49	2	2026-02-06	2026-02-07	16.12	2.00	\N	\N	1	4	PLANIFIEE	\N	VAR-2827
251	52	4	2025-12-10	2025-12-11	80.00	1.00	\N	\N	1	4	TERMINEE	8	\N
291	54	6	2026-02-19	2026-02-20	32.00	4.00	\N	\N	1	5	PLANIFIEE	\N	ROB-9040
271	49	2	2026-01-30	2026-02-06	16.00	2.00	\N	\N	1	4	PLANIFIEE	\N	VAR-5282
272	49	2	2026-01-30	2026-01-31	16.00	2.00	\N	\N	1	4	PLANIFIEE	\N	VAR-8399
276	49	3	2026-01-01	2026-01-02	16.00	2.00	\N	\N	1	\N	PLANIFIEE	\N	VAR-7691
282	49	2	2026-02-05	2026-02-06	16.10	2.00	\N	\N	1	4	PLANIFIEE	\N	VAR-1380
286	52	2	2026-02-03	2026-02-04	8.00	1.00	\N	\N	3	4	PLANIFIEE	\N	NID-9117
288	52	2	2026-02-04	2026-02-05	8.00	1.00	\N	\N	1	4	PLANIFIEE	\N	NID-1578
292	54	5	2026-02-11	2026-02-12	32.00	4.00	\N	\N	1	4	PLANIFIEE	\N	ROB-7947
249	98	1	2025-12-01	2025-12-02	16.00	2.00	\N	\N	3	12	TERMINEE	6	\N
257	2	1	2026-02-01	2026-02-02	99.00	12.38	\N	\N	1	2	PLANIFIEE	\N	\N
258	100	1	2026-01-21	2026-01-22	8.00	1.00	\N	\N	1	12	PLANIFIEE	\N	\N
259	99	1	2025-12-31	2025-12-31	5.00	0.63	\N	\N	3	12	PLANIFIEE	\N	\N
260	98	1	2025-12-31	2025-12-31	2.00	0.25	\N	\N	3	1	PLANIFIEE	\N	\N
261	54	1	2025-12-31	2026-01-01	2.00	0.25	\N	\N	3	2	PLANIFIEE	\N	\N
262	37	1	2026-01-01	2026-01-01	3.00	0.38	\N	\N	3	2	PLANIFIEE	\N	\N
263	53	1	2026-01-02	2026-01-23	3.00	0.38	\N	\N	3	2	PLANIFIEE	\N	\N
264	81	1	2026-01-01	2026-01-14	5.00	0.63	\N	\N	1	4	PLANIFIEE	\N	\N
265	81	1	2026-01-13	2026-01-28	45.00	5.63	\N	\N	1	2	PLANIFIEE	\N	\N
273	98	12	2026-01-01	2026-01-08	16.50	2.06	\N	\N	3	4	PLANIFIEE	\N	JAV-9798
277	49	3	2026-01-01	2026-01-01	16.00	2.00	\N	\N	1	\N	PLANIFIEE	\N	VAR-1040
283	49	2	2026-02-13	2026-02-14	16.00	2.00	\N	\N	1	5	PLANIFIEE	\N	VAR-8041
287	100	3	2026-02-13	2026-02-14	16.00	2.00	\N	\N	1	3	PLANIFIEE	\N	SQL-8571
289	54	2	2026-02-13	2026-02-16	32.00	4.00	\N	\N	1	4	PLANIFIEE	\N	ROB-3783
\.


--
-- Data for Name: session_formation_audit; Type: TABLE DATA; Schema: public; Owner: s3m_user
--

COPY public.session_formation_audit (id_audit, id_session, statut_avant, statut_apres, modifie_par, date_modification, commentaire) FROM stdin;
1	3	PLANIFIEE	EN_COURS	test@s3m.com	2025-12-25 15:25:25.371824	\N
2	3	EN_COURS	TERMINEE	test@s3m.com	2025-12-25 15:26:59.938034	\N
3	2	PLANIFIEE	EN_COURS	test@s3m.com	2025-12-29 13:13:45.282638	\N
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: s3m_user
--

COPY public.users (id, email, password_hash, role, created_at, updated_at, id_entreprise, prenom, nom) FROM stdin;
1	admin@s3m.com	$2a$10$B9ZvdCjqhi9z8oOmzclYruV4Fqo9a7NKUEJJyDFYDn/RyXqr1TA26	ADMIN	2026-01-05 10:34:18.750384	2026-01-05 10:34:18.750384	12	louay	alami
2	client2@s3m.com	$2a$10$B9ZvdCjqhi9z8oOmzclYruV4Fqo9a7NKUEJJyDFYDn/RyXqr1TA26	ADMIN	2026-01-13 13:49:32.36782	2026-01-13 13:49:32.36782	1	Youssef	Sbai
\.


--
-- Name: cout_formation_id_cout_seq; Type: SEQUENCE SET; Schema: public; Owner: s3m_user
--

SELECT pg_catalog.setval('public.cout_formation_id_cout_seq', 73, true);


--
-- Name: demande_reservation_id_demande_seq; Type: SEQUENCE SET; Schema: public; Owner: s3m_user
--

SELECT pg_catalog.setval('public.demande_reservation_id_demande_seq', 8, true);


--
-- Name: departement_id_departement_seq; Type: SEQUENCE SET; Schema: public; Owner: s3m_user
--

SELECT pg_catalog.setval('public.departement_id_departement_seq', 53, true);


--
-- Name: employe_id_employe_seq; Type: SEQUENCE SET; Schema: public; Owner: s3m_user
--

SELECT pg_catalog.setval('public.employe_id_employe_seq', 1158, true);


--
-- Name: entreprise_id_entreprise_seq; Type: SEQUENCE SET; Schema: public; Owner: s3m_user
--

SELECT pg_catalog.setval('public.entreprise_id_entreprise_seq', 15, true);


--
-- Name: evaluation_a_chaud_id_eval_chaud_seq; Type: SEQUENCE SET; Schema: public; Owner: s3m_user
--

SELECT pg_catalog.setval('public.evaluation_a_chaud_id_eval_chaud_seq', 1, false);


--
-- Name: evaluation_a_froid_id_eval_froid_seq; Type: SEQUENCE SET; Schema: public; Owner: s3m_user
--

SELECT pg_catalog.setval('public.evaluation_a_froid_id_eval_froid_seq', 5, true);


--
-- Name: fiche_technique_formation_id_fiche_seq; Type: SEQUENCE SET; Schema: public; Owner: s3m_user
--

SELECT pg_catalog.setval('public.fiche_technique_formation_id_fiche_seq', 93, true);


--
-- Name: formateur_id_formateur_seq; Type: SEQUENCE SET; Schema: public; Owner: s3m_user
--

SELECT pg_catalog.setval('public.formateur_id_formateur_seq', 3, true);


--
-- Name: formation_id_formation_seq; Type: SEQUENCE SET; Schema: public; Owner: s3m_user
--

SELECT pg_catalog.setval('public.formation_id_formation_seq', 108, true);


--
-- Name: participation_id_participation_seq; Type: SEQUENCE SET; Schema: public; Owner: s3m_user
--

SELECT pg_catalog.setval('public.participation_id_participation_seq', 1066, true);


--
-- Name: session_formation_audit_id_audit_seq; Type: SEQUENCE SET; Schema: public; Owner: s3m_user
--

SELECT pg_catalog.setval('public.session_formation_audit_id_audit_seq', 3, true);


--
-- Name: session_formation_id_session_seq; Type: SEQUENCE SET; Schema: public; Owner: s3m_user
--

SELECT pg_catalog.setval('public.session_formation_id_session_seq', 292, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: s3m_user
--

SELECT pg_catalog.setval('public.users_id_seq', 2, true);


--
-- Name: cout_formation cout_formation_pkey; Type: CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.cout_formation
    ADD CONSTRAINT cout_formation_pkey PRIMARY KEY (id_cout);


--
-- Name: demande_reservation demande_reservation_pkey; Type: CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.demande_reservation
    ADD CONSTRAINT demande_reservation_pkey PRIMARY KEY (id_demande);


--
-- Name: departement departement_id_entreprise_nom_key; Type: CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.departement
    ADD CONSTRAINT departement_id_entreprise_nom_key UNIQUE (id_entreprise, nom);


--
-- Name: departement departement_pkey; Type: CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.departement
    ADD CONSTRAINT departement_pkey PRIMARY KEY (id_departement);


--
-- Name: employe employe_pkey; Type: CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.employe
    ADD CONSTRAINT employe_pkey PRIMARY KEY (id_employe);


--
-- Name: entreprise entreprise_pkey; Type: CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.entreprise
    ADD CONSTRAINT entreprise_pkey PRIMARY KEY (id_entreprise);


--
-- Name: evaluation_a_chaud evaluation_a_chaud_pkey; Type: CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.evaluation_a_chaud
    ADD CONSTRAINT evaluation_a_chaud_pkey PRIMARY KEY (id_eval_chaud);


--
-- Name: evaluation_a_froid evaluation_a_froid_pkey; Type: CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.evaluation_a_froid
    ADD CONSTRAINT evaluation_a_froid_pkey PRIMARY KEY (id_eval_froid);


--
-- Name: fiche_technique_formation fiche_technique_formation_pkey; Type: CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.fiche_technique_formation
    ADD CONSTRAINT fiche_technique_formation_pkey PRIMARY KEY (id_fiche);


--
-- Name: formateur formateur_email_key; Type: CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.formateur
    ADD CONSTRAINT formateur_email_key UNIQUE (email);


--
-- Name: formateur formateur_pkey; Type: CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.formateur
    ADD CONSTRAINT formateur_pkey PRIMARY KEY (id_formateur);


--
-- Name: formation formation_pkey; Type: CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.formation
    ADD CONSTRAINT formation_pkey PRIMARY KEY (id_formation);


--
-- Name: participation participation_pkey; Type: CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.participation
    ADD CONSTRAINT participation_pkey PRIMARY KEY (id_participation);


--
-- Name: session_formation_audit session_formation_audit_pkey; Type: CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.session_formation_audit
    ADD CONSTRAINT session_formation_audit_pkey PRIMARY KEY (id_audit);


--
-- Name: session_formation session_formation_id_demande_key; Type: CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.session_formation
    ADD CONSTRAINT session_formation_id_demande_key UNIQUE (id_demande);


--
-- Name: session_formation session_formation_pkey; Type: CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.session_formation
    ADD CONSTRAINT session_formation_pkey PRIMARY KEY (id_session);


--
-- Name: session_formation session_formation_reference_session_key; Type: CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.session_formation
    ADD CONSTRAINT session_formation_reference_session_key UNIQUE (reference_session);


--
-- Name: employe uq_employe_matricule; Type: CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.employe
    ADD CONSTRAINT uq_employe_matricule UNIQUE (matricule);


--
-- Name: entreprise uq_entreprise_nom; Type: CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.entreprise
    ADD CONSTRAINT uq_entreprise_nom UNIQUE (nom_entreprise);


--
-- Name: fiche_technique_formation uq_fiche_formation_version; Type: CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.fiche_technique_formation
    ADD CONSTRAINT uq_fiche_formation_version UNIQUE (id_formation, version_numero);


--
-- Name: participation uq_participation_employe_session; Type: CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.participation
    ADD CONSTRAINT uq_participation_employe_session UNIQUE (id_employe, id_session);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: uq_fiche_active; Type: INDEX; Schema: public; Owner: s3m_user
--

CREATE UNIQUE INDEX uq_fiche_active ON public.fiche_technique_formation USING btree (id_formation) WHERE ((statut)::text = 'ACTIVE'::text);


--
-- Name: session_formation trg_check_session_statut_transition; Type: TRIGGER; Schema: public; Owner: s3m_user
--

CREATE TRIGGER trg_check_session_statut_transition BEFORE UPDATE OF statut ON public.session_formation FOR EACH ROW EXECUTE FUNCTION public.check_session_statut_transition();


--
-- Name: departement departement_id_entreprise_fkey; Type: FK CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.departement
    ADD CONSTRAINT departement_id_entreprise_fkey FOREIGN KEY (id_entreprise) REFERENCES public.entreprise(id_entreprise);


--
-- Name: session_formation_audit fk_audit_session; Type: FK CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.session_formation_audit
    ADD CONSTRAINT fk_audit_session FOREIGN KEY (id_session) REFERENCES public.session_formation(id_session);


--
-- Name: cout_formation fk_cout_session; Type: FK CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.cout_formation
    ADD CONSTRAINT fk_cout_session FOREIGN KEY (id_session) REFERENCES public.session_formation(id_session);


--
-- Name: demande_reservation fk_demande_entreprise; Type: FK CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.demande_reservation
    ADD CONSTRAINT fk_demande_entreprise FOREIGN KEY (id_entreprise) REFERENCES public.entreprise(id_entreprise);


--
-- Name: demande_reservation fk_demande_fiche; Type: FK CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.demande_reservation
    ADD CONSTRAINT fk_demande_fiche FOREIGN KEY (id_fiche) REFERENCES public.fiche_technique_formation(id_fiche);


--
-- Name: demande_reservation fk_demande_formation; Type: FK CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.demande_reservation
    ADD CONSTRAINT fk_demande_formation FOREIGN KEY (id_formation) REFERENCES public.formation(id_formation);


--
-- Name: employe fk_employe_departement; Type: FK CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.employe
    ADD CONSTRAINT fk_employe_departement FOREIGN KEY (id_departement) REFERENCES public.departement(id_departement);


--
-- Name: employe fk_employe_entreprise; Type: FK CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.employe
    ADD CONSTRAINT fk_employe_entreprise FOREIGN KEY (id_entreprise) REFERENCES public.entreprise(id_entreprise);


--
-- Name: employe fk_employe_manager; Type: FK CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.employe
    ADD CONSTRAINT fk_employe_manager FOREIGN KEY (id_manager) REFERENCES public.employe(id_employe);


--
-- Name: evaluation_a_chaud fk_eval_chaud_participation; Type: FK CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.evaluation_a_chaud
    ADD CONSTRAINT fk_eval_chaud_participation FOREIGN KEY (id_participation) REFERENCES public.participation(id_participation);


--
-- Name: evaluation_a_froid fk_eval_froid_n1; Type: FK CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.evaluation_a_froid
    ADD CONSTRAINT fk_eval_froid_n1 FOREIGN KEY (id_n_plus_1) REFERENCES public.employe(id_employe);


--
-- Name: evaluation_a_froid fk_eval_froid_participation; Type: FK CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.evaluation_a_froid
    ADD CONSTRAINT fk_eval_froid_participation FOREIGN KEY (id_participation) REFERENCES public.participation(id_participation);


--
-- Name: fiche_technique_formation fk_fiche_formation; Type: FK CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.fiche_technique_formation
    ADD CONSTRAINT fk_fiche_formation FOREIGN KEY (id_formation) REFERENCES public.formation(id_formation) ON DELETE CASCADE;


--
-- Name: formateur fk_formateur_fournisseur; Type: FK CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.formateur
    ADD CONSTRAINT fk_formateur_fournisseur FOREIGN KEY (id_fournisseur) REFERENCES public.entreprise(id_entreprise);


--
-- Name: participation fk_participation_employe; Type: FK CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.participation
    ADD CONSTRAINT fk_participation_employe FOREIGN KEY (id_employe) REFERENCES public.employe(id_employe);


--
-- Name: participation fk_participation_session; Type: FK CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.participation
    ADD CONSTRAINT fk_participation_session FOREIGN KEY (id_session) REFERENCES public.session_formation(id_session);


--
-- Name: session_formation fk_session_demande; Type: FK CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.session_formation
    ADD CONSTRAINT fk_session_demande FOREIGN KEY (id_demande) REFERENCES public.demande_reservation(id_demande);


--
-- Name: session_formation fk_session_entreprise; Type: FK CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.session_formation
    ADD CONSTRAINT fk_session_entreprise FOREIGN KEY (id_entreprise) REFERENCES public.entreprise(id_entreprise);


--
-- Name: session_formation fk_session_formateur; Type: FK CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.session_formation
    ADD CONSTRAINT fk_session_formateur FOREIGN KEY (id_formateur) REFERENCES public.formateur(id_formateur);


--
-- Name: session_formation fk_session_formation; Type: FK CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.session_formation
    ADD CONSTRAINT fk_session_formation FOREIGN KEY (id_formation) REFERENCES public.formation(id_formation);


--
-- Name: session_formation fk_session_fournisseur; Type: FK CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.session_formation
    ADD CONSTRAINT fk_session_fournisseur FOREIGN KEY (id_fournisseur) REFERENCES public.entreprise(id_entreprise);


--
-- Name: users fk_users_entreprise; Type: FK CONSTRAINT; Schema: public; Owner: s3m_user
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fk_users_entreprise FOREIGN KEY (id_entreprise) REFERENCES public.entreprise(id_entreprise) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- PostgreSQL database dump complete
--

\unrestrict mOuVuBqwXCPiXZlJNilLVUyVElXDWawQ1XsRwhNnFSB0eR9jT8NDquKoA8QLrec

