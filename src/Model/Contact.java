package Model;

/** Contact Class. */
public class Contact {

    private int id;
    private String name;
    private String email;

    /** Contact Class.
     * Creates a new contact object.
     * @param id int Contact ID
     * @param name String Contact Name
     * @param email String Contact Email
     */
    public Contact(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    /** Overrides to string to change combo-box display
     * @return country name
     */
    @Override public String toString() {
        return this.name;
    }

    /** Get ID
     *  Gets Contact ID
     * @return ID
     */
    public int getId() {
        return id;
    }

    /** Get Name
     * Gets Contact Name
     * @return name
     */
    public String getName() {
        return name;
    }
}
