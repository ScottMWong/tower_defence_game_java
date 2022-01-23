import bagel.DrawOptions;
import bagel.Font;
import bagel.Image;
import bagel.util.Colour;

/**
 * Class for the status panel.
 * Handles rendering the status panel.
 */
public class StatusPanel {
    private Image image;
    private Font font;
    private String wave = "Wave: ";
    private String timescale = "Timescale: ";
    private String status = "Status: ";
    private String lives = "Lives: ";
    private DrawOptions greentext;
    private DrawOptions whitetext;
    /**
     * Constructor for the status panel.
     */
    StatusPanel() {
        image = new Image("res/images/statuspanel.png");
        font = new Font("res/fonts/DejaVuSans-Bold.ttf",16);
        greentext = new DrawOptions();
        whitetext = new DrawOptions();
        greentext.setBlendColour(Colour.GREEN);
        whitetext.setBlendColour(Colour.WHITE);
    }

    /**
     * Renders the status panel on the screen.
     * All information needed is provided by the ShadowDefend instance.
     * @param wavenum  is the current wave number
     * @param timescalesetting  is the current timescale
     * @param gamestatus  is the current game status
     * @param lifecount  is the number of lives the player has.
     */
    protected void StatusPanelRender(int wavenum, int timescalesetting, Status gamestatus, int lifecount) {
        wave = "Wave: " + wavenum;
        timescale = "Timescale: " + timescalesetting + ".0";
        lives = "Lives: " + lifecount;
        switch (gamestatus) {
            case Winner:
                status = "Status: Winner!";
                break;
            case Placing:
                status = "Status: Placing";
                break;
            case WaveStarted:
                status = "Status: Wave In Progress";
                break;
            case Waiting:
                status = "Status: Awaiting Start";
                break;
        }

        image.drawFromTopLeft(0,743);
        font.drawString(wave,15,765, whitetext);
        if (timescalesetting == 1) {
            font.drawString(timescale,200,765, whitetext);
        }
        else {
            font.drawString(timescale,200,765, greentext);
        }
        font.drawString(status,450,765, whitetext);
        font.drawString(lives,900,765, whitetext);
    }
}
