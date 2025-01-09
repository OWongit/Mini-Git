import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class RepositoryTest {
    private Repository repo1;
    private Repository repo2;

    /**
     * NOTE: The following test suite assumes that getRepoHead(), commit(), and size()
     *       are implemented correctly.
     */

    @BeforeEach
    public void setUp() {
        repo1 = new Repository("repo1");
        repo2 = new Repository("repo2");
        Repository.Commit.resetIds();
    }

    @Test
    @DisplayName("Test getHistory()")
    public void getHistory() {
        // Initialize commit messages
        String[] commitMessages = new String[]{"Initial commit.", "Updated method documentation.",
                                                "Removed unnecessary object creation."};

        // Commit the commit messages to repo1
        for (int i = 0; i < commitMessages.length; i++) {
            String commitMessage = commitMessages[i];
            repo1.commit(commitMessage);

            // Assert that the current commit id is at the repository's head
            // We know our ids increment from 0, meaning we can just use i as our id
            assertEquals("" + i, repo1.getRepoHead());
        }

        assertEquals(repo1.getRepoSize(), commitMessages.length);

        // This is the method we are testing for. First, we'll obtain the 2 most recent commits
        // that have been made to repo1.
        String repositoryHistory = repo1.getHistory(2);
        String[] commits = repositoryHistory.split("\n");

        // Verify that getHistory() only returned 2 commits.
        assertEquals(commits.length, 2);

        // Verify that the 2 commits have the correct commit message and commit id
        for (int i = 0; i < commits.length; i++) {
            String commit = commits[i];

            // Old commit messages/ids are on the left and the more recent commit messages/ids are
            // on the right so need to traverse from right to left to ensure that 
            // getHistory() returned the 2 most recent commits.
            int backwardsIndex = (commitMessages.length - 1) - i;
            String commitMessage = commitMessages[backwardsIndex];

            assertTrue(commit.contains(commitMessage));
            assertTrue(commit.contains("" + backwardsIndex));
        }
    }

    @Test
    @DisplayName("Test drop() (empty case)")
    public void testDropEmpty() {
        assertFalse(repo1.drop("123"));
    }

    @Test
    @DisplayName("Test drop() (front case)")
    public void testDropFront() {
        assertEquals(repo1.getRepoSize(), 0);
        // Initialize commit messages
        String[] commitMessages = new String[]{"First commit.", "Added unit tests."};

        // Commit to repo1 - ID = "0"
        repo1.commit(commitMessages[0]);

        // Commit to repo2 - ID = "1"
        repo2.commit(commitMessages[1]);

        // Assert that repo1 successfully dropped "0"
        assertTrue(repo1.drop("0"));
        assertEquals(repo1.getRepoSize(), 0);
        
        // Assert that repo2 does not drop "0" but drops "1"
        // (Note that the commit ID increments regardless of the repository!)
        assertFalse(repo2.drop("0"));
        assertTrue(repo2.drop("1"));
        assertEquals(repo2.getRepoSize(), 0);
    }

    @Test
    @DisplayName("Test getRepoHead()")
    public void testGetRepoHead() {
        // Initialize commit messages
        String[] commitMessages = new String[]{"First commit.", "Second commit."};

        // Commit to repo1 - ID = "0"
        repo1.commit(commitMessages[0]);

        // Commit to repo1 - ID = "1"
        repo1.commit(commitMessages[1]);

        // Assert that getRepoHead() gets the correct Repository head
        assertEquals(repo1.getRepoHead(), "1");
        assertEquals(repo1.drop("1"), true);
        assertEquals(repo1.getRepoHead(), "0");
    }

    @Test
    @DisplayName("Test getRepoSize()")
    public void testGetRepoSize() {
        // Initialize commit messages
        String[] commitMessages = new String[]{"First commit.", "Second commit."};

        // Assert the Repository size is 0
        assertEquals(repo1.getRepoSize(), 0);

        // Commit to repo1 - ID = "0"
        repo1.commit(commitMessages[0]);

        // Assert the Repository size is 1
        assertEquals(repo1.getRepoSize(), 1);

        // Commit to repo1 - ID = "1"
        repo1.commit(commitMessages[1]);

        // Assert the Repository size is 2
        assertEquals(repo1.getRepoSize(), 2);

        // Drops the current head
        assertEquals(repo1.drop("1"), true);

        // Assert the Repository size is 1
        assertEquals(repo1.getRepoSize(), 1);
    }

    @Test
    @DisplayName("Test contains()")
    public void testContains() {
        // Initialize commit messages
        String[] commitMessages = new String[]{"First commit.", "Second commit."};

        // Commit to repo1 - ID = "0"
        repo1.commit(commitMessages[0]);

        // Commit to repo1 - ID = "1"
        repo1.commit(commitMessages[1]);

        // Assert that contains() returns the correct boolean value
        assertTrue(repo1.contains("0"));
        assertTrue(repo1.contains("1"));
        assertFalse(repo1.contains("2"));
    }

    @Test
    @DisplayName("Test Synchronize() (both full case)")
    public void testSynchronize0() {
        assertEquals(repo1.getRepoSize(), 0);
        assertEquals(repo2.getRepoSize(), 0);

        // Initialize commit messages
        String[] commitMessages = new String[]{"First commit.", "Second commit.",
                                                "Third commit.", "Fourth commit"};

        // Commit to repo1 - ID = "0"
        repo1.commit(commitMessages[0]);

        // Commit to repo1 - ID = "1"
        repo1.commit(commitMessages[1]);

        // Commit to repo1 - ID = "2"
        repo1.commit(commitMessages[2]);

        // Commit to repo1 - ID = "3"
        repo1.commit(commitMessages[3]);

        // Synchronizes repo2 onto repo1
        repo1.synchronize(repo2);

        // Assert that repo1 contains all 4 IDs in the correct order
        assertEquals(repo1.getRepoHead(), "3");
        assertEquals(repo1.drop("3"), true);
        assertEquals(repo1.getRepoHead(), "2");
        assertEquals(repo1.drop("2"), true);
        assertEquals(repo1.getRepoHead(), "1");
        assertEquals(repo1.drop("1"), true);
        assertEquals(repo1.getRepoHead(), "0");
        assertEquals(repo1.drop("0"), true);

        // Assert that repo2 head is null.
        assertEquals(repo2.getRepoHead(), null);
    }

    @Test
    @DisplayName("Test Synchronize() (this empty, other full)")
    public void testSynchronize1() {
        assertEquals(repo1.getRepoSize(), 0);
        assertEquals(repo2.getRepoSize(), 0);

        // Initialize commit messages
        String[] commitMessages = new String[]{"First commit.", "Second commit."};

        // Commit to repo1 - ID = "0"
        repo2.commit(commitMessages[0]);

        // Commit to repo1 - ID = "1"
        repo2.commit(commitMessages[1]);

        // Synchronizes repo2 onto repo1
        repo1.synchronize(repo2);

        // Assert that repo1 contains the 2 IDs in the correct order
        assertEquals(repo1.getRepoHead(), "1");
        assertEquals(repo1.drop("1"), true);
        assertEquals(repo1.getRepoHead(), "0");
        assertEquals(repo1.drop("0"), true);

        // Assert that repo2 head is null.
        assertEquals(repo2.getRepoHead(), null);
    }

    @Test
    @DisplayName("Test Synchronize() (this full, other empty)")
    public void testSynchronize2() {
        assertEquals(repo1.getRepoSize(), 0);
        assertEquals(repo2.getRepoSize(), 0);

        // Initialize commit messages
        String[] commitMessages = new String[]{"First commit.", "Second commit."};

        // Commit to repo1 - ID = "0"
        repo1.commit(commitMessages[0]);

        // Commit to repo1 - ID = "1"
        repo1.commit(commitMessages[1]);

        // Synchronizes repo2 onto repo1
        repo1.synchronize(repo2);

        // Assert that repo1 contains the 2 IDs in the correct order
        assertEquals(repo1.getRepoHead(), "1");
        assertEquals(repo1.drop("1"), true);
        assertEquals(repo1.getRepoHead(), "0");
        assertEquals(repo1.drop("0"), true);

        // Assert that repo2 head is null.
        assertEquals(repo2.getRepoHead(), null);
    }
}
