package behnen.julia.makeyourownadventure;

import junit.framework.TestCase;

import behnen.julia.makeyourownadventure.model.StoryHeader;

/**
 * Tests the StoryHeader class.
 *
 * @author Julia Behnen
 * @version December 6, 2015
 */
public class StoryHeaderTest extends TestCase {

    private static final String AUTHOR = "test User";
    private static final String STORY_ID = "test StoryId";
    private static final String TITLE = "test Title";
    private static final String DESCRIPTION = "test Descriptio'&n";

    private StoryHeader mStoryHeader;

    public void setUp() {
        mStoryHeader = new StoryHeader(AUTHOR, STORY_ID, TITLE, DESCRIPTION);
    }

    public void testConstructor() {
        StoryHeader header = new StoryHeader(AUTHOR, STORY_ID, TITLE, DESCRIPTION);
        assertNotNull(header);
    }

    public void testGetAuthor() {
        assertEquals(AUTHOR, mStoryHeader.getAuthor());
    }

    public void testGetStoryId() {
        assertEquals(STORY_ID, mStoryHeader.getStoryId());
    }

    public void testGetTitle() {
        assertEquals(TITLE, mStoryHeader.getTitle());
    }

    public void testGetDescription() {
        assertEquals(DESCRIPTION, mStoryHeader.getDescription());
    }

    public void testToString() {
        assertEquals("{\"author\":\"" + AUTHOR + "\",\"story_id\":\"" + STORY_ID +
                "\",\"title\":\"" + TITLE + "\",\"description\":\"" + DESCRIPTION + "\"}",
                mStoryHeader.toString());
    }

    public void testParseJSONWithValidString() {
        StoryHeader storyHeader = StoryHeader.parseJson(mStoryHeader.toString());
        assertEquals(mStoryHeader.toString(), storyHeader.toString());
    }

    public void testParseJSONWithInvalidString() {
        StoryHeader storyHeader = StoryHeader.parseJson("dfasfdsafdsaqtgrsgrwe");
        assertEquals(null, storyHeader);
    }
}
