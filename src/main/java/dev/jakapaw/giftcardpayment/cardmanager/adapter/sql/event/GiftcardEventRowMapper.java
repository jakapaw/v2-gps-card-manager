package dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.event;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GiftcardEventRowMapper implements RowMapper<GiftcardEvent> {
    @Override
    public GiftcardEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new GiftcardEvent(
                rs.getLong("card_id"),
                rs.getInt("version"),
                rs.getString("event_name"),
                rs.getLong("balance_change"),
                rs.getString("event_data")
        );
    }
}
