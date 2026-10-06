CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS unaccent;
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- CREATE OR REPLACE FUNCTION places_tile(z integer, x integer, y integer)
-- RETURNS bytea AS $$
--   WITH t AS (
--     SELECT id, name, category,
--            ST_AsMVTGeom(location::geometry, ST_TileEnvelope(z,x,y), 4096, 64, true) AS geom
--     FROM places
--     WHERE status = 'APPROVED'
--       AND location::geometry && ST_TileEnvelope(z,x,y)
--   )
-- SELECT ST_AsMVT(t, 'places', 4096, 'geom') FROM t WHERE geom IS NOT NULL;
-- $$ LANGUAGE sql STABLE PARALLEL SAFE;