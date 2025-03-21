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
	SELECT ges.balance, ges.last_version INTO snap_balance,snap_version FROM giftcard_event_snapshot ges WHERE ges.card_id = $1;
	SELECT
        COALESCE(snap_balance, 0) + SUM(balance_change), MAX("version") INTO current_balance,last_version
    FROM giftcard_event WHERE card_id = $1 AND "version" > COALESCE(snap_version, 0);
END;
';
