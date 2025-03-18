DROP FUNCTION IF EXISTS rebuild_state(giftcard_event.card_id%TYPE);
CREATE FUNCTION rebuild_state (
    giftcard_event.card_id%TYPE
) RETURNS BIGINT LANGUAGE PLPGSQL RETURNS NULL ON NULL INPUT AS
'
DECLARE
    current_balance BIGINT;
BEGIN
    SELECT SUM(balance_change) INTO current_balance FROM giftcard_event WHERE card_id = $1;
    RETURN current_balance;
END;
';