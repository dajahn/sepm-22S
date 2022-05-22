package at.ac.tuwien.sepm.groupphase.backend.util;

public class SqlStringConverter {

    public String toSqlString(String string) {
        String result = null;
        if (string != null) {
            result = '%' + string + '%';
        }
        return result;
    }
}
