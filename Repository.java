import java.util.*;
// Owen Wilhere
// 02/7/2024
// CSE 123
// TA: Jay Dharmadhikari
// This class is Repository. It keeps track modifactions and the history
// of documents.

import java.text.SimpleDateFormat;

public class Repository {

    private String name;
    private int size;
    private Commit head;

    // Constructs the class Repository
    // Paramaters: String name - the name of the repository
    // Throws an IllegalArgumentException if name == null or .equls("")
    public Repository(String name){
        if(name == null || name.equals("")){
            throw new IllegalArgumentException("name is empty or null");
        }
        this.name = name;
        this.size = 0;
        this.head = null;
    }

    // Returns the head commit of a repository(String)
    public String getRepoHead(){
        if(head == null){
            return null;
        }
        return head.id;
    }

    // Returns the size of a repository(int)
    public int getRepoSize(){
        return size;
    }

    // Returns a toString representation of a repository(String)
    public String toString(){
        if(size == 0){
            return name + " - No commits";
        }
        return name + " - Current head: " + head;
    }

    // Determines if a repository contains a certain commit ID
    // Paramaters:  -String targetId: the ID being looked for
    // Returns True if the repository contains the ID returns false
    // if it doesn't.
    public boolean contains(String targetId){
        Commit current = head;
        while (current != null) {
            if(current.id.equals(targetId)){
                return true;
            }
            current = current.past;
        }
        return false;
    }

    // Returns a list of all the commit messages that have been commited
    // to the repository(String). The method will return the entire history
    // if there less than n commits in the method. Returns an empty String
    // if the repository is empty.
    // Parameters: int n - how far back in the history the method returns
    // Throws an IllegalArgumentException if n is equal to or less than zero.
    public String getHistory(int n){
        if(n <= 0){
            throw new IllegalArgumentException("inputted number must be positive");
        }
        String listString = "";
        Commit current = head;
        while (current != null && n > 0) {
            listString += current + "\n";
            current = current.past;
            n--;
        }
        return listString;
    }

    // Commits a message to a Repository
    // Paramters: String message - the message that the commit contains
    // Returns the ID of the commit(String)
    public String commit(String message){
        Commit current = new Commit(message);
        current.past = head;
        head = current;
        this.size++;
        return head.id;
    }

    // Removes a commit from the repository
    // Parameters: String targetId - the ID of the commit to be Removed
    // Returns true if the commit was succesfully dropped returns false
    // if not.
    public boolean drop(String targetId){
        if (head != null) {
            if (head.id.equals(targetId)) {
                head = head.past;
                size--;
                return true;
            } else {
                Commit curr = head;
                while (curr.past != null && curr.past.id != targetId) {
                    curr = curr.past;
                }
                if (curr.past != null) {
                    curr.past = curr.past.past;
                    size--;
                    return true;
                }
            }
        }
        return false;
    }

    // Adds the history of a different repository to the repository
    // the method is being called from and empties the commit history
    // of the other repository. Orders the commit history based off 
    // of the time they were called. 
    // Paramaters: Repository other - the other repository that's history 
    //                                is being added
    // No returns.
    public void synchronize(Repository other){
        this.size = this.size + other.size;
        if(this.head == null){
            this.head = other.head;
            other.head = null;
        } else if(other.head != null){
            if(other.head.timeStamp > this.head.timeStamp){
                Commit point = other.head.past;
                other.head.past = this.head;
                this.head = other.head;
                other.head = point;
            }

            Commit curr = this.head;
            while(curr.past != null && other.head != null){
                if(other.head.timeStamp > curr.past.timeStamp){
                    Commit point = other.head.past;
                    other.head.past = curr.past;
                    curr.past = other.head;
                    other.head = point;
                }

                curr = curr.past;
            }

            if(curr.past == null){
                curr.past = other.head;
                other.head = null;
            }
        }
    }

    /**
     * DO NOT MODIFY
     * A class that represents a single commit in the repository.
     * Commits are characterized by an identifier, a commit message,
     * and the time that the commit was made. A commit also stores
     * a reference to the immediately previous commit if it exists.
     *
     * Staff Note: You may notice that the comments in this 
     * class openly mention the fields of the class. This is fine 
     * because the fields of the Commit class are public. In general, 
     * be careful about revealing implementation details!
     */
    public class Commit {

        private static int currentCommitID;

        /**
         * The time, in milliseconds, at which this commit was created.
         */
        public final long timeStamp;

        /**
         * A unique identifier for this commit.
         */
        public final String id;

        /**
         * A message describing the changes made in this commit.
         */
        public final String message;

        /**
         * A reference to the previous commit, if it exists. Otherwise, null.
         */
        public Commit past;

        /**
         * Constructs a commit object. The unique identifier and timestamp
         * are automatically generated.
         * @param message A message describing the changes made in this commit.
         * @param past A reference to the commit made immediately before this
         *             commit.
         */
        public Commit(String message, Commit past) {
            this.id = "" + currentCommitID++;
            this.message = message;
            this.timeStamp = System.currentTimeMillis();
            this.past = past;
        }

        /**
         * Constructs a commit object with no previous commit. The unique
         * identifier and timestamp are automatically generated.
         * @param message A message describing the changes made in this commit.
         */
        public Commit(String message) {
            this(message, null);
        }

        /**
         * Returns a string representation of this commit. The string
         * representation consists of this commit's unique identifier,
         * timestamp, and message, in the following form:
         *      "[identifier] at [timestamp]: [message]"
         * @return The string representation of this collection.
         */
        @Override
        public String toString() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(timeStamp);

            return id + " at " + formatter.format(date) + ": " + message;
        }

        /**
        * Resets the IDs of the commit nodes such that they reset to 0.
        * Primarily for testing purposes.
        */
        public static void resetIds() {
            Commit.currentCommitID = 0;
        }
    }
}
