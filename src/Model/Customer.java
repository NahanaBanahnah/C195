package Model;

import java.sql.Timestamp;

/** Customer Class */
public class Customer {
    private int id;
    private String name;
    private String address;
    private String postalCode;
    private String phone;
    private String division;
    private String country;
    private int dId;
    private int cId;
    private Timestamp created;
    private Timestamp updatedDate;
    private String updatedBy;
    private String createdBy;

    /** Create Customer Object.
     * Creates a new customer object
     * @param id Int auto gen customer ID
     * @param name String customer name
     * @param address String customer address
     * @param postalCode String customer postal code
     * @param phone String customer phone number
     * @param division String division name
     * @param country String country name
     * @param dId int Division ID
     * @param cId int country ID
     **/
    public Customer(int id, String name, String address, String postalCode, String phone, String division, String country, int dId, int cId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.division = division;
        this.country = country;
        this.dId = dId;
        this.cId = cId;
    }

    /** Get ID
     * Gets Customer ID
     * @return id
     */
    public int getId() {
        return id;
    }

    /** Set ID
     * Sets Customer ID
     * @param id id
     */
    public void setId(int id) {
        this.id = id;
    }

    /** Get Name
     * Gets Customer Name
     * @return name
     */
    public String getName() {
        return name;
    }

    /** Get Address
     * gets address
     * @return
     */
    public String getAddress() {
        return address;
    }

    /** Get postal code
     * gets postal code
     * @return
     */
    public String getPostalCode() {
        return postalCode;
    }

    /** Get Phone
     * gets phone number
     * @return
     */
    public String getPhone() {
        return phone;
    }

    /** Get Division
     * gets Division Name
     * @return
     */
    public String getDivision() {
        return division;
    }

    /** Get Country
     * gets country name
     * @return
     */
    public String getCountry() {
        return country;
    }

    /** Get division ID
     * gets Division ID
     * @return
     */
    public int getdId() {
        return dId;
    }

    /** Get Country ID
     * gets country
     * @return
     */
    public int getcId() {
        return cId;
    }

    /** Overrides to string to change combo-box display
     * @return country name
     */
    @Override public String toString() {
        return this.name;
    }

}
