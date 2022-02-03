package Model;

/** Division Class. */
public class Division {
    private int divisionId;
    private String division;
    private int countryId;

    /** Creates a new Division Object.
     * @param divisionId int Division ID
     * @param division String division name
     */
    public Division(int divisionId, String division, int countryId) {
        this.divisionId = divisionId;
        this.division = division;
        this.countryId = countryId;
    }

    /** Override to change Combo Box text.
     *
     * @return
     */
    @Override public String toString() {
        return this.division;
    }

    public int getDivisionId() {
        return this.divisionId;
    }

    public String getDivision() {
        return this.division;
    }

    public int getCountryId() {
        return this.countryId;
    }
}
