--
-- PostgreSQL database dump
--

-- Dumped from database version 16.2
-- Dumped by pg_dump version 16.2

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
-- Data for Name: role; Type: TABLE DATA; Schema: public; Owner: travelcarry
--

INSERT INTO public.role (id, name) VALUES (1, 'ROLE_USER');
INSERT INTO public.role (id, name) VALUES (2, 'ROLE_ADMIN');


--
-- Name: role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: travelcarry
--

SELECT pg_catalog.setval('public.role_id_seq', 2, true);


--
-- PostgreSQL database dump complete
--

