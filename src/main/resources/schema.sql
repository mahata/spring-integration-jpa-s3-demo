CREATE TABLE IF NOT EXISTS name
(
    id   UUID NOT NULL DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    CONSTRAINT uuid_table_pkey PRIMARY KEY (id)
);
