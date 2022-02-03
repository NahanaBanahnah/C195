package Model;

/** Country Class. */
public class Country {

    private int id;
    private String country;

    /** Creates Country Object
     * @param id int country ID
     * @param country String Country Name
     */
    public Country(int id, String country) {
        this.id = id;
        this.country = country;
    }

    /** Overrides to string to change combo-box display
     * @return country name
     */
    @Override public String toString() {
        return this.country;
    }

    /** Get the country's id
     * @return int country ID
     */
    public int getId() {
        return this.id;
    }

    /** Get the country's Name
     * @return String Country Name
     */
    public String getCountry() {
        return this.country;
    }
}
