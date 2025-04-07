package vn.xuanhung.ELearning_Service.common;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public enum ParseHelper {

    INT {
        @Override
        public Integer parse(Object value) {
            if (value instanceof Integer) {
                return (Integer) value;
            } else if (value instanceof Long) {
                return ((Long) value).intValue();
            } else if (value instanceof String) {
                try {
                    return Integer.parseInt((String) value);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            else if(value instanceof BigDecimal){
                return ((BigDecimal) value).intValue();
            }

            return null;
        }
    },
    LONG {
        @Override
        public Long parse(Object value) {
            if (value instanceof Long) {
                return (Long) value;
            } else if (value instanceof Integer) {
                return ((Integer) value).longValue();
            } else if (value instanceof String) {
                try {
                    return Long.parseLong((String) value);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    },
    STRING {
        @Override
        public String parse(Object value) {
            return value != null ? value.toString() : null;
        }
    },
    BOOLEAN {
        @Override
        public Boolean parse(Object value) {
            if (value instanceof Boolean) {
                return (Boolean) value;
            } else if (value instanceof Integer) {
                return ((Integer) value) != 0;
            } else if (value instanceof String) {
                return Boolean.parseBoolean((String) value);
            }
            return null;
        }
    },
    BIGDECIMAL {
        @Override
        public BigDecimal parse(Object value) {
            if (value instanceof BigDecimal) {
                return (BigDecimal) value;
            } else if (value instanceof String) {
                try {
                    return new BigDecimal((String) value);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else if (value instanceof Double) {
                return BigDecimal.valueOf((Double) value);
            } else if (value instanceof Long) {
                return BigDecimal.valueOf((Long) value);
            } else if (value instanceof Integer) {
                return BigDecimal.valueOf((Integer) value);
            }
            return null;
        }
    },
    LOCALDATE {
        @Override
        public LocalDate parse(Object value) {
            if (value instanceof LocalDate) {
                return (LocalDate) value;
            } else if (value instanceof String) {
                try {
                    return LocalDate.parse((String) value, DateTimeFormatter.ISO_DATE);
                } catch (DateTimeParseException e) {
                    e.printStackTrace();
                }
            } else if (value instanceof Date) {
                // Convert java.util.Date to LocalDate
                return  LocalDate.ofInstant(Instant.ofEpochMilli(((Date) value).getTime()), ZoneId.systemDefault());
            }
            return null;
        }
    },
    INSTANT {
        @Override
        public Instant parse(Object value) {
            if (value instanceof Instant) {
                return (Instant) value;
            } else if (value instanceof String) {
                try {
                    return Instant.parse((String) value); // ISO 8601 format
                } catch (DateTimeParseException e) {
                    e.printStackTrace();
                }
            } else if (value instanceof Date) {
                // Convert java.util.Date to Instant
                return ((Date) value).toInstant();
            }
            return null;
        }
    };

    public abstract <T> T parse(Object value);
}
