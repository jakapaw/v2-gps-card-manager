DROP FUNCTION IF EXISTS rebuild_state(
    IN giftcard_event.card_id%TYPE,
    OUT current_balance INT8,
    INOUT last_version INT4
);

create or REPLACE FUNCTION rebuild_state (
    IN giftcard_event.card_id%TYPE,
    OUT current_balance INT8,
    INOUT last_version INT4
) LANGUAGE PLPGSQL RETURNS NULL ON NULL INPUT AS
'
DECLARE
	snap_balance INT8;
	snap_version INT4;
BEGIN
	WITH rowset1 AS(
		SELECT
	        SUM(balance_change) as balance_change, MAX("version") as "version"
	    FROM giftcard_event WHERE card_id = $1 AND "version" > COALESCE(snap_version, 0)
	)
	SELECT COALESCE(balance_change, 0), "version" INTO current_balance, last_version FROM rowset1;
END;
';
