package wrteam.ecart.shop.model;

public class SavedLocationAddress  {
    // name and description.
    private String placeName;

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public SavedLocationAddress() {
    }

    public SavedLocationAddress(String placeName) {
        this.placeName = placeName;
    }
}
