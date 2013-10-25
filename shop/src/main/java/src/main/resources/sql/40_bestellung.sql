-- ===============================================================================
-- Jede SQL-Anweisung muss in genau 1 Zeile
-- Kommentare durch -- am Zeilenanfang
-- ===============================================================================


INSERT INTO bestellung (id, kunde_fk, idx, erzeugt, aktualisiert) VALUES (400,101,0,to_timestamp('01.08.2006 00:00:00','DD.MM.YYYY HH24:MI:SS'),to_timestamp('01.08.2006 00:00:00','DD.MM.YYYY HH24:MI:SS'));
INSERT INTO bestellung (id, kunde_fk, idx, erzeugt, aktualisiert) VALUES (401,101,1,to_timestamp('02.08.2006 00:00:00','DD.MM.YYYY HH24:MI:SS'),to_timestamp('02.08.2006 00:00:00','DD.MM.YYYY HH24:MI:SS'));
INSERT INTO bestellung (id, kunde_fk, idx, erzeugt, aktualisiert) VALUES (402,102,0,to_timestamp('03.08.2006 00:00:00','DD.MM.YYYY HH24:MI:SS'),to_timestamp('03.08.2006 00:00:00','DD.MM.YYYY HH24:MI:SS'));
INSERT INTO bestellung (id, kunde_fk, idx, erzeugt, aktualisiert) VALUES (403,102,1,to_timestamp('04.08.2006 00:00:00','DD.MM.YYYY HH24:MI:SS'),to_timestamp('04.08.2006 00:00:00','DD.MM.YYYY HH24:MI:SS'));
INSERT INTO bestellung (id, kunde_fk, idx, erzeugt, aktualisiert) VALUES (404,104,0,to_timestamp('05.08.2006 00:00:00','DD.MM.YYYY HH24:MI:SS'),to_timestamp('05.08.2006 00:00:00','DD.MM.YYYY HH24:MI:SS'));
