import bagel.DrawOptions;
import bagel.Font;
import bagel.Image;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Class for the buy panel.
 * Handles rendering for the buy panel and logic for selection
 * of tower types from the purchase buttons.
 */
public class BuyPanel {
    private Image image;
    private Image tank;
    private Image supertank;
    private Image aircraft;
    private Font fontsmall;
    private Font fontmedium;
    private Font fontlarge;
    private final String tankcost = "$250";
    private final String supertankcost = "$600";
    private final String aircraftcost = "$500";
    private final String keybinda = "Key binds:";
    private final String keybindb = "S - Start Wave";
    private final String keybindc = "L - Increase Timescale";
    private final String keybindd = "K - Decrease Timescale";
    private String moneytotal;
    private final Rectangle tankselect;
    private final Rectangle supertankselect;
    private final Rectangle aircraftselect;
    private DrawOptions greentext;
    private DrawOptions redtext;
    private DrawOptions whitetext;

    /**
     * Constructor for the BuyPanel class.
     */
    BuyPanel() {
        image = new Image("res/images/buypanel.png");
        fontsmall = new Font("res/fonts/DejaVuSans-Bold.ttf",16);
        fontmedium = new Font("res/fonts/DejaVuSans-Bold.ttf",20);
        fontlarge = new Font("res/fonts/DejaVuSans-Bold.ttf",40);
        tank = new Image("res/images/tank.png");
        supertank = new Image("res/images/supertank.png");
        aircraft = new Image("res/images/airsupport.png");
        moneytotal = "$500";
        tankselect = new Rectangle(64,8,64,64);
        supertankselect = new Rectangle(184,8,64,64);
        aircraftselect = new Rectangle(304,8,64,64);
        greentext = new DrawOptions();
        redtext = new DrawOptions();
        whitetext = new DrawOptions();
        greentext.setBlendColour(Colour.GREEN);
        redtext.setBlendColour(Colour.RED);
        whitetext.setBlendColour(Colour.WHITE);
    }

    /**
     * Renders the buy panel on the screen.
     * @param money  is the amount of money the player has, given by the Shadow Defend instance
     */
    protected void BuyPanelRender(int money) {
        moneytotal = "$" + money;
        image.drawFromTopLeft(0,0);
        tank.drawFromTopLeft(64,8);
        supertank.drawFromTopLeft(184,8);
        aircraft.drawFromTopLeft(304,8);

        fontsmall.drawString(keybinda,400,12, whitetext);
        fontsmall.drawString(keybindb,400,36, whitetext);
        fontsmall.drawString(keybindc,400,52, whitetext);
        fontsmall.drawString(keybindd,400,68, whitetext);

        fontlarge.drawString(moneytotal,824,65, whitetext);

        if (money >= 250) {
            fontmedium.drawString(tankcost,64,90, greentext);
        }
        else {
            fontmedium.drawString(tankcost,64,90, redtext);
        }

        if (money >= 600) {
            fontmedium.drawString(supertankcost,184,90, greentext);
        }
        else {
            fontmedium.drawString(supertankcost,184,90, redtext);
        }
        if (money >= 500) {
            fontmedium.drawString(aircraftcost,304,90, greentext);
        }
        else {
            fontmedium.drawString(aircraftcost,304,90, redtext);
        }

        }

    /**
     * Checks to see if a buy button was clicked, and whether the player has the funds to purchase.
     * @param clickpoint  is the position of the mouse click to check.
     * @param money  is the amount of money the player has, given by the ShadowDefend instance
     * @return  returns type type of tower clicked if the purchase is valid, or none otherwise.
     */
    protected ClickStatus BuyPanelClick(Point clickpoint, int money) {
        if (tankselect.intersects(clickpoint) && money >= 250) {
            return ClickStatus.tank;
        }

        else if (supertankselect.intersects(clickpoint) && money >= 600) {
            return ClickStatus.supertank;
        }

        else if (aircraftselect.intersects(clickpoint) && money >= 500) {
            return ClickStatus.aircraft;
        }
        return ClickStatus.none;
    }
}
