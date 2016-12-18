package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@DatabaseTable(tableName = "clients")
@Data
public class Client {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String company;

    @DatabaseField
    private String mobileNumber;

    @DatabaseField
    private String email;

    @DatabaseField
    private String address;


    public Client() {
        // ORMLite needs a no-arg constructor
    }

    public boolean isValid() {
        return StringUtils.isNotBlank(name) &&
                    StringUtils.isNotBlank(mobileNumber);
    }

}
