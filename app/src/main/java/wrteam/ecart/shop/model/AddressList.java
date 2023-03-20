package wrteam.ecart.shop.model;

public class AddressList {
    // Store the name of the movie
    private String mainText;
    // Store the release date of the movie
    private String secondaryText;

    public AddressList(String mainText, String secondaryText) {
        this.mainText = mainText;
        this.secondaryText = secondaryText;
    }

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public String getSecondaryText() {
        return secondaryText;
    }

    public void setSecondaryText(String secondaryText) {
        this.secondaryText = secondaryText;
    }
}
