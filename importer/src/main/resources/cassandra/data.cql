CREATE KEYSPACE IF NOT EXISTS tailnumber
WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : '1' };

DROP TABLE tailnumber.registrations;

CREATE TABLE tailnumber.registrations (
	id text PRIMARY KEY,
	country text,
	lastUpdate date,
	record text
)
WITH COMPRESSION = { 'class': 'ZstdCompressor'};
