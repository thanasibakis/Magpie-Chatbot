import java.util.*;

/**
 * A class that performs analysis of the grammar of a statement.
 *
 * @author	Thanasi Bakis
 */
public class Grammar
{
    private static final String[] MODAL_AUXILIARIES  = {"can", "could", "may", "might", "must", "shall", "should", "will", "would"},
                           		  QUESTION_WORDS     = {"who", "what", "when", "where", "how", "which"},
                          		  SUBJECT_PRONOUNS   = {"i", "you", "he", "she", "it", "that", "we", "they"},
                           		  OBJECT_PRONOUNS    = {"me", "you", "him", "her", "it", "that", "us", "them"},
                          		  BE_VERBS           = {"am", "are", "is", "is", "is", "is", "are", "are"};
	
    /**
     * A question is a statement where one of the following exists:
     *     - a modal auxiliary followed by a subject pronoun
     *     - a be verb followed by a subject pronoun
     *     - a question word
	 *
     * @param statement     a statement that may be a question
     * @return              whether the statement is a question
	 * @version 2016.05.26
     */
    public static boolean isQuestion(String statement)
    {
        for(String word : MODAL_AUXILIARIES)
		{
            for(String pronoun : SUBJECT_PRONOUNS)
			{
				String wordAfterWord = wordAfter(statement, word);
				
                if(pronoun.equals(wordAfterWord))
				{
                    return true;
				}
			}
		}
		
        for(String word : BE_VERBS)
		{
            for(String pronoun : SUBJECT_PRONOUNS)
			{
				String wordAfterWord = wordAfter(statement, word);
				
                if(pronoun.equals(wordAfterWord))
				{
                    return true;
				}
			}
		}
		
        for(String word : QUESTION_WORDS)
		{
            if(findKeyword(statement, word) >= 0)
			{
                return true;
			}
		}
		
        return statement.indexOf('?') >= 0;
    }
    
    /**
     * Finds the word after a specified word in a statment.
	 *
     * @param statement     the statement that contains the word that is being searched for
     * @param word          the specified word that lies before the unknown word that the user is looking for
     * @return              the word after the specified word
	 * @version 2016.05.26
     */
    public static String wordAfter(String statement, String word)
    {
		int positionOfWord = findKeyword(statement, word);
		
        if(positionOfWord < 0)
		{
			return "";
		}
		
        for(int i = positionOfWord + word.length() + 1; i < statement.length(); i++)
        {
            if(statement.charAt(i) == ' ')
			{
				return statement.substring(positionOfWord + word.length() + 1, i);
			}
            else if(i == statement.length() - 1)
			{
				return statement.substring(positionOfWord + word.length() + 1);
			}
        }
        return "";
    }
    
    /**
     * Finds the word after a specified word in a statment.
	 *
     * @param statement     the statement that contains the word that is being searched for
     * @param word          the specified word that lies after the unknown word that the user is looking for
     * @return              the word before the specified word
	 * @version 2016.05.26
     */
    public static String wordBefore(String statement, String word)
    {
		int positionOfWord = findKeyword(statement, word);
		
        if(positionOfWord <= 1)
		{
			return "";
		}
		
        for(int i = positionOfWord - 2; i >= 0; i--)
        {
            if(statement.charAt(i) == ' ')
			{
				return statement.substring(i + 1, positionOfWord - 1);
			}
            else if(i == 0)
			{
				return statement.substring(0, positionOfWord - 1);
			}
        }
        return "";
    }
    
    /**
     * Replaces all contractions in a statement with their expanded form.
	 *
     * @param statement     the statement that may contain contractions
     * @return              the statement where all contractions have been expanded
	 * @version 2016.05.26
     */
    public static String removeContractions(String statement)
    {
		int positionOfApostrophe = statement.indexOf("'");
		
        if(positionOfApostrophe < 0)
		{
			return statement;
		}
		
        for(int i = positionOfApostrophe - 1; i >= 0; i--)
        {
            String subject, beforeContraction, afterContraction;
			
            if(statement.charAt(i) == ' ')
            {
                subject = statement.substring(i + 1, positionOfApostrophe);
                beforeContraction = statement.substring(0, i + 1);
                afterContraction = statement.substring(statement.indexOf(' ', positionOfApostrophe));
				
                if(subject.equals("don"))
				{
					String newStatement = beforeContraction + "do not" + afterContraction;
					return removeContractions(newStatement);
				}
                else if(subject.equals("can"))
				{
					String newStatement = beforeContraction + "cannot" + afterContraction;
					return removeContractions(newStatement);
				}
                else if(subject.equals("aren"))
				{
					String newStatement = beforeContraction + "are not" + afterContraction;
					return removeContractions(newStatement);
				}
				else if(listContainsItem(SUBJECT_PRONOUNS, subject))
				{
					String newVerb = BE_VERBS[indexOfItemInList(SUBJECT_PRONOUNS, subject)];
					String newStatement = beforeContraction + subject + ' ' + newVerb + afterContraction;
					return removeContractions(newStatement);
				}
				else
				{
					String newStatement = beforeContraction + subject + " is" + afterContraction;
					return removeContractions(newStatement);
				}
            }
            else if(i == 0)
            {
                subject = statement.substring(0, positionOfApostrophe);
                beforeContraction = "";
                afterContraction = statement.substring(statement.indexOf(' ', positionOfApostrophe));
				
                if(subject.equals("don"))
				{
					String newStatement = beforeContraction + "do not" + afterContraction;
					return removeContractions(newStatement);
				}
                else if(subject.equals("can"))
				{
					String newStatement = beforeContraction + "cannot" + afterContraction;
					return removeContractions(newStatement);
				}
                else if(subject.equals("aren"))
				{
					String newStatement = beforeContraction + "are not" + afterContraction;
					return removeContractions(newStatement);
				}
				else if(listContainsItem(SUBJECT_PRONOUNS, subject))
				{
					String newVerb = BE_VERBS[indexOfItemInList(SUBJECT_PRONOUNS, subject)];
					String newStatement = beforeContraction + subject + ' ' + newVerb + afterContraction;
					return removeContractions(newStatement);
				}
				else
				{
					String newStatement = beforeContraction + subject + " is" + afterContraction;
					return removeContractions(newStatement);
				}
            }
        }
		
        if(positionOfApostrophe < 0)
		{
			return statement;
		}
		
        String subject = statement.substring(0, positionOfApostrophe);
		String afterContraction = statement.substring(statement.indexOf(' ', positionOfApostrophe));
		String newVerb = BE_VERBS[indexOfItemInList(SUBJECT_PRONOUNS, subject)];
		String newStatement = subject + ' ' + newVerb + afterContraction;
		
        return removeContractions(newStatement);
    }
    
    /**
     * Take the restOfStatement from a transformation and change the pronouns from the first person point of view to the second person point of view.
	 *
     * @param statement     the statement in the first person point of view
     * @return              statement in the second person point of view
	 * @version 2016.05.26
     */
    public static String invertPointOfView(String statement)
    {
        String answer = statement;
		
        while(containsSubjectPronoun(statement))
        {
            String actualSubject = findSubjectPronoun(statement);
			String invertedSubject = actualSubject;
            int positionOfSubject = findKeyword(statement, actualSubject);
			
            switch(actualSubject)
            {
                case "i":
                case "we":
					invertedSubject = "you";
					break;
                case "you":
					invertedSubject = "I";
					break;
            }
			
			String beforeSubject = answer.substring(0, positionOfSubject);
			String afterSubject = answer.substring(positionOfSubject + actualSubject.length());
			
            answer =  beforeSubject + invertedSubject + afterSubject;
			
			String placeholder = generateStars(invertedSubject.length());
			beforeSubject = statement.substring(0, positionOfSubject);
			afterSubject = statement.substring(positionOfSubject + actualSubject.length());
			
            statement = beforeSubject + placeholder + afterSubject;
            statement = statement.trim();
        }
		
        while(containsObjectPronoun(statement))
        {
            String actualObject = findObjectPronoun(statement);
			String invertedObject = actualObject;
            int positionOfObject = findKeyword(statement, actualObject);
			
            switch(actualObject)
            {
                case "me":
                case "us":
					invertedObject = "you";
					break;
                case "you":
					invertedObject = "me";
					break;
            }
			
			String beforeObject = answer.substring(0, positionOfObject);
			String afterObject = answer.substring(positionOfObject + actualObject.length());
			
            answer =  beforeObject + invertedObject + afterObject;
			
			String placeholder = generateStars(invertedObject.length());
			beforeObject = statement.substring(0, positionOfObject);
			afterObject = statement.substring(positionOfObject + actualObject.length());
			
            statement = beforeObject + placeholder + afterObject;
            statement = statement.trim();
        }
		
        while(containsBeVerb(statement))
        {
            String actualVerb = findBeVerb(statement);
			String invertedVerb = actualVerb;
            int positionOfVerb = findKeyword(statement, actualVerb);
			
            if(isQuestion(answer))
            {
				String wordAfterVerb = wordAfter(answer, actualVerb);
				
                switch(wordAfterVerb)
                {
                    case "I":
						invertedVerb = "am";
						break;
                    case "you":
						invertedVerb = "are";
						break;
                }
            }
            else
            {
				String wordBeforeVerb = wordBefore(answer, actualVerb);
				
                switch(wordBeforeVerb)
                {
                    case "I":
						invertedVerb = "am";
						break;
                    case "you":
						invertedVerb = "are";
						break;
                }
            }
			
			String beforeVerb = answer.substring(0, positionOfVerb);
			String afterVerb = answer.substring(positionOfVerb + actualVerb.length());
			
            answer = beforeVerb + invertedVerb + afterVerb;
			
			String placeholder = generateStars(invertedVerb.length());
			beforeVerb = statement.substring(0, positionOfVerb);
			afterVerb = statement.substring(positionOfVerb + actualVerb.length());
			
            statement = beforeVerb + placeholder + afterVerb;
            statement = statement.trim();
        }
		
        return answer;
    }
    
    /**
     * Search for one word in phrase. 
	 * The search is not case sensitive.
     * This method will check that the given goal is not a substring of a longer string.
     * For example, "I know" does not contain "no".
	 *
     * @param statement     the string to search
     * @param goal          the string to search for
     * @param startPos      the character of the string to begin the search at
     * @return              the index of the first occurrence of goal in statement or -1 if it's not found
	 * @version 2016.05.26
     */
    public static int findKeyword(String statement, String goal, int startPos)
    {
        int position = statement.toLowerCase().indexOf(goal.toLowerCase(), startPos);

        while(position >= 0) 
        {
            String before = " ";
			String after = " ";
			
            if(position > 0)
			{
                before = statement.substring(position - 1, position).toLowerCase();
			}
			
            if(position + goal.length() < statement.length())
			{
                after = statement.substring(position + goal.length(), position + goal.length() + 1).toLowerCase();
			}

            //  If before and after aren't letters, we've found the word
            if(((before.compareTo("a") < 0 ) || (before.compareTo("z") > 0))
			&& ((after.compareTo("a") < 0 ) || (after.compareTo("z") > 0)))
			{
                return position;
			}

            //  The last position didn't work, so let's find the next, if there is one.
            position = statement.indexOf(goal.toLowerCase(), position + 1);
        }

        return -1;
    }

    /**
     * Search for one word in phrase.
	 * The search is not case sensitive.
     * This method will check that the given goal is not a substring of a longer string.
     * For example, "I know" does not contain "no".
	 * The search begins at the beginning of the string.
	 *
     * @param statement     the string to search
     * @param goal          the string to search for
     * @return              the index of the first occurrence of goal in statement or -1 if it's not found
	 * @version 2016.05.26
     */
    public static int findKeyword(String statement, String goal)
    {
        return findKeyword(statement, goal, 0);
    }
    
    /**
     * Finds the first modal auxiliary in a statement.
     * If one is not found, returns 1000 stars to represent 'null' without throwing an exception anywhere.
	 *
     * @param statement     the statement that may contain a modal auxiliary
     * @return              the first modal auxiliary in the statement, or 1000 stars if one is not found
	 * @version 2016.05.26
     */
    public static String findModalAuxiliary(String statement)
    {
        for(String word : MODAL_AUXILIARIES)
		{
			int indexOfWord = findKeyword(statement, word);
			
            if(indexOfWord >= 0)
			{
                return word;
			}
		}
		
        return generateStars(1000);
    }
    
    /**
     * Returns whether the statement has a modal auxiliary.
	 *
     * @param statement     the statement that may contain a modal auxiliary
     * @return              whether the statement has a modal auxiliary
	 * @version 2016.05.26
     */
    public static boolean containsModalAuxiliary(String statement)
    {
        return !findModalAuxiliary(statement).equals(generateStars(1000));
    }
    
    /**
     * Finds the first question word in a statement.
     * If one is not found, returns 1000 stars to represent 'null' without throwing an exception anywhere.
	 *
     * @param statement     the statement that may contain a question word
     * @return              the first question word in the statement, or 1000 stars if one is not found
	 * @version 2016.05.26
     */
    public static String findQuestionWord(String statement)
    {
        for(String word : QUESTION_WORDS)
		{
			int indexOfWord = findKeyword(statement, word);
			
            if(indexOfWord >= 0)
			{
                return word;
			}
		}
		
        return generateStars(1000);
    }
    
    /**
     * Returns whether the statement has a question word.
	 *
     * @param statement     the statement that may contain a question word
     * @return              whether the statement has a question word
	 * @version 2016.05.26
     */
    public static boolean containsQuestionWord(String statement)
    {
        return !findQuestionWord(statement).equals(generateStars(1000));
    }
    
    /**
     * Finds the first subject pronoun in a statement.
     * If one is not found, returns 1000 stars to represent 'null' without throwing an exception anywhere
     * @param statement     the statement that may contain a subject pronoun
     * @return              the first subject pronoun in the statement, or 1000 stars if one is not found
	 * @version 2016.05.26
     */
    public static String findSubjectPronoun(String statement)
    {
        for(String word : SUBJECT_PRONOUNS)
		{
			int indexOfWord = findKeyword(statement, word);
			
            if(indexOfWord >= 0)
			{
                return word;
			}
		}
		
        return generateStars(1000);
    }
    
    /**
     * Returns whether the statement has a subject pronoun.
	 *
     * @param statement     the statement that may contain a subject pronoun
     * @return              whether the statement has a subject pronoun
	 * @version 2016.05.26
     */
    public static boolean containsSubjectPronoun(String statement)
    {
        return !findSubjectPronoun(statement).equals(generateStars(1000));
    }
    
    /**
     * Finds the first object pronoun in a statement.
     * If one is not found, returns 1000 stars to represent 'null' without throwing an exception anywhere.
	 *
     * @param statement     the statement that may contain a object pronoun
     * @return              the first object pronoun in the statement, or 1000 stars if one is not found
	 * @version 2016.05.26
     */
    public static String findObjectPronoun(String statement)
    {
        for(String word : OBJECT_PRONOUNS)
		{
			String subjectPronoun = findSubjectPronoun(statement);
			int positionOfSubjectPronoun = findKeyword(statement, subjectPronoun);
			int positionOfWordAfterSubjectPronoun = findKeyword(statement, word, positionOfSubjectPronoun);
			
            if(positionOfWordAfterSubjectPronoun >= 0)
			{
                return word;
			}
		}
		
        return generateStars(1000);
    }
    
    /**
     * Returns whether the statement has a object pronoun.
	 *
     * @param statement     the statement that may contain a object pronoun
     * @return              whether the statement has a object pronoun
	 * @version 2016.05.26
     */
    public static boolean containsObjectPronoun(String statement)
    {
        return !findObjectPronoun(statement).equals(generateStars(1000));
    }

    /**
     * Finds the first be verb in a statement.
     * If one is not found, returns 1000 stars to represent 'null' without throwing an exception anywhere.
	 *
     * @param statement     the statement that may contain a be verb
     * @return              the first be verb in the statement, or 1000 stars if one is not found
	 * @version 2016.05.26
     */
    public static String findBeVerb(String statement)
    {
        for(String word : BE_VERBS)
		{
			int positionOfWord = findKeyword(statement, word);
			
            if(positionOfWord >= 0)
			{
                return word;
			}
		}
		
        return generateStars(1000);
    }
    
    /**
     * Returns whether the statement has a be verb.
	 *
     * @param statement     the statement that may contain a be verb
     * @return              whether the statement has a be verb
	 * @version 2016.05.26
     */
    public static boolean containsBeVerb(String statement)
    {
        return !findBeVerb(statement).equals(generateStars(1000));
    }
    
    /**
     * Generates a string of star characters of a given length.
	 *
     * @param num           the number of stars to be returned
     * @return              a string with num star characters
	 * @version 2016.05.26
     */
    private static String generateStars(int num)
    {
        String ans = "";
		
        for(int i = 0; i < num; i++)
		{
			ans += "*";
		}
		
        return ans;
    }
	
    /**
     * Determines the index of an <code>Object</code> in an array.
	 *
     * @param list          the array to search through
	 * @param item			the <code>Object</code> to search for
     * @return              the index of <code>item</code> in <code>list</code>
	 * @version 2016.05.26
     */
	private static int indexOfItemInList(Object[] list, Object item)
	{
		return Arrays.asList(list).indexOf(item);
	}
	
    /**
     * Searches for an <code>Object</code> in an array.
	 *
     * @param list          the array to search through
	 * @param item			the <code>Object</code> to search for
     * @return              whether <code>item</code> is in <code>list</code>
	 * @version 2016.05.26
     */
	private static boolean listContainsItem(Object[] list, Object item)
	{
		return indexOfItemInList(list, item) >= 0;
	}
}