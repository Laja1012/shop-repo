-- ===============================================================================
-- Jede SQL-Anweisung muss in genau 1 Zeile
-- Kommentare durch -- am Zeilenanfang
-- ===============================================================================


INSERT INTO wartungsvertrag (nr, datum, inhalt, kunde_fk, idx, erzeugt, aktualisiert) VALUES (1,to_date('31.01.2005','DD.MM.YYYY'),'Wartungsvertrag_1_K101',101,0,to_timestamp('01.08.2006 00:00:00','DD.MM.YYYY HH24:MI:SS'),to_timestamp('01.08.2006 00:00:00','DD.MM.YYYY HH24:MI:SS'));
INSERT INTO wartungsvertrag (nr, datum, inhalt, kunde_fk, idx, erzeugt, aktualisiert) VALUES (2,to_date('31.01.2006','DD.MM.YYYY'),'Wartungsvertrag_2_K101',101,1,to_timestamp('02.08.2006 00:00:00','DD.MM.YYYY HH24:MI:SS'),to_timestamp('02.08.2006 00:00:00','DD.MM.YYYY HH24:MI:SS'));
INSERT INTO wartungsvertrag (nr, datum, inhalt, kunde_fk, idx, erzeugt, aktualisiert) VALUES (1,to_date('30.06.2006','DD.MM.YYYY'),'Wartungsvertrag_1_K102',102,0,to_timestamp('03.08.2006 00:00:00','DD.MM.YYYY HH24:MI:SS'),to_timestamp('03.08.2006 00:00:00','DD.MM.YYYY HH24:MI:SS'));
