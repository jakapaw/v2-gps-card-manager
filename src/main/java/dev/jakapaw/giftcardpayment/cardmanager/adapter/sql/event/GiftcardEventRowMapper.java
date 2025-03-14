package dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.event;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class GiftcardEventRowMapper implements RowMapper<GiftcardEvent> {
    @Override
    public GiftcardEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
        LocalDateTime time = null;
        if (rs.getTimestamp("created_at") != null) {
            time = rs.getTimestamp("created_at").toLocalDateTime();
        }
        return new GiftcardEvent(
                rs.getLong("card_id"),
                rs.getInt("version"),
                rs.getString("event_name"),
                rs.getLong("balance_change"),
                rs.getString("event_data"),
                time
        );
    }
}
