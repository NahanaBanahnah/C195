package Model;

import java.sql.Timestamp;

/** Appointment Class */
public class Appointment {
    private int id;
    private String title;
    private String desc;
    private String location;
    private String type;
    private Timestamp start;
    private Timestamp end;
    private int contactId;
    private String contact;
    private int customerId;
    private String customer;
    private int userID;


    /** New Appointment
     * Creates a new appointment
     * @param id Appointment ID
     * @param title Appointment Title
     * @param desc Description
     * @param location Location
     * @param type Appointment Type
     * @param start Start Date and Time
     * @param end End Date and Time
     * @param contactId Contact ID
     * @param contact Contact Name
     * @param customerId Customer ID
     * @param customer Customer Name
     * @param userID User ID
     */
    public Appointment(int id, String title, String desc, String location, String type, Timestamp start, Timestamp end, int contactId, String contact, int customerId, String customer, int userID) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.contactId = contactId;
        this.contact = contact;
        this.customerId = customerId;
        this.customer = customer;
        this.userID = userID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public int getUserID() {
        return userID;
    }
}
