import java.util.*;
import java.io.*;

/**
 * A program to carry on conversations with a human user.
 *
 * @author	Thanasi Bakis
 */
public class Magpie
{
	private Scanner in;
	private List<String> items, information;
	
	private final String[] RANDOM_RESPONSES = {"Interesting, tell me more.",
											  "Hmmm.",
											  "Do you really think so?",
											  "You don't say.",
											  "I didn't know that.",
											  "That's cool."};

	/**
	 * Creates a new <code>Magpie</code> chatbot that receives input from the user through <code>stdin</code>.
	 * @version 2016.05.26
	 */
	public Magpie()
	{
		in = new Scanner(System.in);
		items = new ArrayList<String>();
		information = new ArrayList<String>();
	}

	/**
	 * Get a default greeting.
	 *
	 * @return			 a greeting
	 * @version 2016.05.26
	 */ 
	public String getGreeting()
	{
		return "Hello.";
	}

	/**
	 * Gives a response to a user statement.
	 * 
	 * @param statement	 the user statement
	 * @return			 a response based on the rules given
	 * @version 2016.05.26
	 */
	public String getResponse(String statement)
	{
		String response = "";
		statement = Grammar.removeContractions(statement.toLowerCase().trim());
		
		if(statement.length() == 0)
		{
			response = "Say something, please.";
		}
		else if(Grammar.findKeyword(statement, "hi") >= 0 
			 || Grammar.findKeyword(statement, "hello") >= 0
			 || Grammar.findKeyword(statement, "hey") >= 0)
		{
			response = "Hello there.";
		}
		else if(Grammar.findKeyword(statement, "you") >= 0
			 && Grammar.findKeyword(statement, "how") >= 0)
		{
			response = "I'm doing well.";
		}
		else if(Grammar.findKeyword(statement, "your") >= 0
			 && Grammar.findKeyword(statement, "name") >= 0)
		{
			response = "I'm Magpie.";
		}
		else if(Grammar.findKeyword(statement, "remember") >= 0)
		{
			response = addToMemory();
		}
		else if(Grammar.findKeyword(statement, "recall") >= 0)
		{
			response = getFromMemory();
		}
		else if(Grammar.findKeyword(statement, "delete") >= 0)
		{
			response = removeFromMemory();
		}
		else if(Grammar.findKeyword(statement, "no") >= 0)
		{
			response = "Why so negative?";
		}
		else if(Grammar.findKeyword(statement, "play") >= 0
			 && Grammar.findKeyword(statement, "game") >= 0)
		{
			response = playGame();
		}
		else if(Grammar.findKeyword(statement, "mother") >= 0
			 || Grammar.findKeyword(statement, "father") >= 0
			 || Grammar.findKeyword(statement, "sister") >= 0
			 || Grammar.findKeyword(statement, "brother") >= 0
			 || Grammar.findKeyword(statement, "family") >= 0)
		{
			response = "Tell me more about your family.";
		}
		else if(Grammar.findKeyword(statement, "dog") >= 0
			 || Grammar.findKeyword(statement, "cat") >= 0)
		{
			response = "Tell me more about your pets.";
		}
		else if(Grammar.findKeyword(statement, "mr. allen") >= 0)
		{
			response = "He sounds like a good teacher.";
		}
		else if(Grammar.findKeyword(statement, "computer science") >= 0)
		{
			if(Grammar.findKeyword(statement, "like") >= 0)
			{
				response = "Yes, it is my favorite course.";
			}
			else
			{
				response = "I love that class.";
			}
		}
		else if(Grammar.findKeyword(statement, "good") >= 0)
		{
			response = "That's good to hear.";
		}
		else if(Grammar.findKeyword(statement, "favorite") >= 0)
		{
			if(Grammar.isQuestion(statement))
			{
				response = "I'm not sure.";
			}
			else
			{
				response = "That's mine, too.";
			}
		}
		else if(Grammar.findKeyword(statement, "birthday") >= 0)
		{
			response = "Happy birthday!";
		}
		else if(Grammar.findKeyword(statement, "feared") >= 0)
		{
			response = "FEARED.";
		}
		else if(Grammar.findKeyword(statement, "questions") >= 0)
		{
			response = "No.";
		}
		// Responses which require transformations
		else if(Grammar.containsBeVerb(statement)
			 && Grammar.containsSubjectPronoun(statement))
		{
			if(Grammar.findKeyword(statement, Grammar.findBeVerb(statement), Grammar.findKeyword(statement, Grammar.findSubjectPronoun(statement))) >= 0)
			{
				response = transformSubjectBeVerbStatement(statement);
			}
			else if(Grammar.findKeyword(statement, Grammar.findSubjectPronoun(statement), Grammar.findKeyword(statement, Grammar.findBeVerb(statement))) >= 0)
			{
				response = transformBeVerbSubjectStatement(statement);
			}
		}
		else if(Grammar.findKeyword(statement, "i want") >= 0)
		{
			if(Grammar.findKeyword(statement, "to", Grammar.findKeyword(statement, "i want")) >= 0)
			{
				response = transformIWantToStatement(statement);
			}
			else
			{
				response = transformIWantStatement(statement);
			}
		}
		else if(Grammar.containsModalAuxiliary(statement) 
			 && Grammar.isQuestion(statement))
		{
			response = transformModalAuxiliaryStatement(statement);
		}
		else if(Grammar.findKeyword(statement, "i") >= 0
			 && Grammar.findKeyword(statement, "you", Grammar.findKeyword(statement, "i")) >= 0)
		{
			response = transformIYouStatement(statement);
		}
		else if(Grammar.findKeyword(statement, "you") >= 0
			 && Grammar.findKeyword(statement, "me", Grammar.findKeyword(statement, "you")) >= 0)
		{
			response = transformYouMeStatement(statement);
		}
		else if(Grammar.findKeyword(statement, "you") >= 0
			 && Grammar.findKeyword(statement, "like", Grammar.findKeyword(statement, "you")) >= 0)
		{
			response = transformYouLikeStatement(statement);
		}
		else
		{
			if(Grammar.isQuestion(statement))
			{
				response = "I'm not sure.";
			}
			else
			{ 
				response = getRandomResponse();
			}
		}
		return response;
	}

	/**
	 * Saves a topic and related information to <code>ArrayList</code> instance variables.
	 *
	 * @return	  a response confirming the action
	 * @version 2016.05.26
	 */
	private String addToMemory()
	{
		System.out.print("What's the item I should remember? ");
		String item = in.nextLine();
		System.out.print("What's the information I should remember? ");
		String info = in.nextLine();
		items.add(item);
		information.add(info);
		
		return "You got it. I'll remember that.";
	}

	/**
	 * Gives information from the <code>ArrayList</code> instance variables.
	 *
	 * @return	  information based on the requested item
	 * @version 2016.05.26
	 */
	private String getFromMemory()
	{
		System.out.print("What's the item I should recall? ");
		String item = in.nextLine();
		
		for(int i = 0; i<items.size(); i++)
		{
			if(items.get(i).equals(item))
			{
				return "\"" + item + "\" is " + information.get(i);
			}
		}
		
		System.out.println("I'm not sure what that is. This is what I know:");
		
		for(String i: items)
		{
			System.out.println("\t" + i);
		}
		
		return "Sorry. Please try again.";
	}

	/**
	 * Removes a topic and related information from the <code>ArrayList</code> instance variables.
	 *
	 * @return	  a response confirming the action
	 * @version 2016.05.26
	 */
	private String removeFromMemory()
	{
		System.out.print("What's the item I should forget? ");
		String item = in.nextLine();
		
		for(int i = 0; i < items.size(); i++)
		{
			if(items.get(i).equals(item))
			{
				items.remove(i);
				information.remove(i);
				
				return "You got it. I'll forget that.";
			}
		}
		
		System.out.println("I'm not sure what that is. This is what I know:");
		
		for(String i: items)
		{
			System.out.println("\t" + i);
		}
		
		return "Sorry. Please try again.";
	}
	
	/**
	 * Take a statement with "<subject> <be verb> <something>." and transform it into "Why <be verb> <subject> <something>?".
	 *
	 * @param statement	 the user statement, assumed to contain a subject pronoun followed by a be verb
	 * @return			 the transformed statement
	 * @version 2016.05.26
	 */
	private String transformSubjectBeVerbStatement(String statement)
	{
		// Remove any final punctuation
		if(statement.charAt(statement.length() - 1) == '?'
		|| statement.charAt(statement.length() - 1) == '.')
		{
			statement = statement.substring(0, statement.length() - 1);
		}
		
		String subject = Grammar.findSubjectPronoun(statement);
		String beVerb = Grammar.findBeVerb(statement);
		int positionOfSubject = Grammar.findKeyword(statement, subject);
		int positionOfBeVerb = Grammar.findKeyword(statement, beVerb, positionOfSubject);
		String restOfStatement = statement.substring(positionOfBeVerb + beVerb.length()).trim();
		String newStatement = "Why " + beVerb + " " + subject + " " + restOfStatement + "?";
		
		return Grammar.invertPointOfView(newStatement);
	}
	
	/**
	 * Take a statement with "<be verb> <subject> <something>?" and transform it into "I don't know if <subject> <be verb> <something>.".
	 *
	 * @param statement	 the user statement, assumed to contain a subject pronoun followed by a be verb
	 * @return			 the transformed statement
	 * @version 2016.05.26
	 */
	private String transformBeVerbSubjectStatement(String statement)
	{
		// Remove any final punctuation
		if(statement.charAt(statement.length() - 1) == '?'
		|| statement.charAt(statement.length() - 1) == '.')
		{
			statement = statement.substring(0, statement.length() - 1);
		}
		
		String beVerb = Grammar.findBeVerb(statement);
		String subject = Grammar.findSubjectPronoun(statement);
		int positionOfBeVerb = Grammar.findKeyword(statement, beVerb);
		int positionOfSubject = Grammar.findKeyword(statement, subject, positionOfBeVerb);
		String restOfStatement = statement.substring(positionOfSubject + subject.length()).trim();
		String newStatement = subject + " " + beVerb + " " + restOfStatement + ".";
		
		return "I don't know if " + Grammar.invertPointOfView(newStatement);
	}
	
	/**
	 * Take a statement with "<modal auxiliary> <subject> <something>?" and transform it into "I don't know, <modal auxiliary> <subject> <something>?".
	 *
	 * @param statement	 the user statement, assumed to contain a modal auxilary
	 * @return			 the transformed statement
	 * @version 2016.05.26
	 */
	private String transformModalAuxiliaryStatement(String statement)
	{
		// Remove any final punctuation
		if(statement.charAt(statement.length() - 1) == '?' || statement.charAt(statement.length() - 1) == '.')
		{
			statement = statement.substring(0, statement.length() - 1);
		}
		
		String aux = Grammar.findModalAuxiliary(statement);
		int positionOfAux = Grammar.findKeyword(statement, aux);
		int positionOfSubject = statement.indexOf(' ', positionOfAux + aux.length() + 1);
		String subject = statement.substring(positionOfAux + aux.length() + 1, positionOfSubject);
		String restOfStatement = statement.substring(positionOfSubject + subject.length()).trim();
		String newStatement = subject + " " + restOfStatement + "?";
		
		return "I don't know, " + aux + " " + Grammar.invertPointOfView(newStatement);
	}

	/**
	 * Take a statement with "I want to <something>." and transform it into "Why do you want to <something>?".
	 *
	 * @param statement	 the user statement, assumed to contain "I want to"
	 * @return			 the transformed statement
	 * @version 2016.05.26
	 */
	private String transformIWantToStatement(String statement)
	{
		// Remove any final punctuation
		if(statement.charAt(statement.length() - 1) == '?'
		|| statement.charAt(statement.length() - 1) == '.')
		{
			statement = statement.substring(0, statement.length() - 1);
		}
		
		int position = Grammar.findKeyword(statement, "i want to");
		String restOfStatement = statement.substring(position + "i want to".length()).trim();
		
		return "Why do you want to " + Grammar.invertPointOfView(restOfStatement) + "?";
	}

	/**
	 * Take a statement with "I want <something>." and transform it into "Would you really be happy if you had <something>?".
	 *
	 * @param statement	 the user statement, assumed to contain "I want"
	 * @return			 the transformed statement
	 * @version 2016.05.26
	 */
	private String transformIWantStatement(String statement)
	{
		// Remove any final punctuation
		if(statement.charAt(statement.length() - 1) == '?'
		|| statement.charAt(statement.length() - 1) == '.')
		{
			statement = statement.substring(0, statement.length() - 1);
		}
		
		int position = Grammar.findKeyword(statement, "i want");
		String restOfStatement = statement.substring(position + "i want".length()).trim();
		
		return "Would you really be happy if you had " + restOfStatement + "?";
	}

	/**
	 * Take a statement with "<subject> like <something>?" and transform it into "I'm not sure if I like <something>.".
	 *
	 * @param statement	 the user statement, assumed to contain "you" followed by "like"
	 * @return			 the transformed statement
	 * @version 2016.05.26
	 */
	private String transformYouLikeStatement(String statement)
	{
		// Remove any final punctuation
		if(statement.charAt(statement.length() - 1) == '?'
		|| statement.charAt(statement.length() - 1) == '.')
		{
			statement = statement.substring(0, statement.length() - 1);
		}
		
		int positionOfYou = Grammar.findKeyword(statement, "you");
		int positionOfLike = Grammar.findKeyword(statement, "like", positionOfYou + "you".length());
		String restOfStatement = statement.substring(positionOfLike + "like".length()).trim();
		
		return "I'm not sure if I like " + restOfStatement + ".";
	}

	/**
	 * Take a statement with "you <something> me" and transform it into "What makes you think that I <something> you?".
	 *
	 * @param statement	 the user statement, assumed to contain "you" followed by "me"
	 * @return			 the transformed statement
	 * @version 2016.05.26
	 */
	private String transformYouMeStatement(String statement)
	{
		// Remove any final punctuation
		if(statement.charAt(statement.length() - 1) == '?'
		|| statement.charAt(statement.length() - 1) == '.')
		{
			statement = statement.substring(0, statement.length() - 1);
		}
		
		int positionOfYou = Grammar.findKeyword(statement, "you");
		int positionOfMe = Grammar.findKeyword(statement, "me", positionOfYou + "you".length());
		String restOfStatement = statement.substring(positionOfYou + "you".length(), positionOfMe).trim();
		String endingObjects = statement.substring(positionOfMe + "me".length()).trim();
		
		return "What makes you think that I " + restOfStatement + " you " + endingObjects + "?";
	}

	/**
	 * Take a statement with "I <something> you" and transform it into "Why do you <something> me?".
	 *
	 * @param statement	 the user statement, assumed to contain "I" followed by "you"
	 * @return			 the transformed statement
	 * @version 2016.05.26
	 */
	private String transformIYouStatement(String statement)
	{
		// Remove any final punctuation
		if(statement.charAt(statement.length() - 1) == '?'
		|| statement.charAt(statement.length() - 1) == '.')
		{
			statement = statement.substring(0, statement.length() - 1);
		}
		
		int positionOfI = Grammar.findKeyword(statement, "i");
		int positionOfYou = Grammar.findKeyword(statement, "you", positionOfI);
		String restOfStatement = statement.substring(positionOfI + "i".length(), positionOfYou).trim();
		
		if(Grammar.containsBeVerb(statement))
		{
			String newStatement = "Why " + restOfStatement + " me?";
			return Grammar.invertPointOfView(newStatement);
		}
		
		return "Why do you " + restOfStatement + " me?";
	}
	
	/**
	 * Runs a "guess the number" game.
	 *
	 * @return	  a response based on the result of the game
	 * @version 2016.05.26
	 */
	private String playGame()
	{
		System.out.println("Ok. Let's play this game: I'll pick a number. You have 5 chances to guess it.");
		System.out.println("What should the lower bound be?");
		int lowerBound = Integer.parseInt(in.nextLine());
		System.out.println("What should the upper bound be?");
		int upperBound = Integer.parseInt(in.nextLine());
		System.out.println("Ok. I chose a number between " + lowerBound + " and " + upperBound + ", inclusive.");
		int answer = lowerBound + (int)(Math.random() * ((upperBound - lowerBound) + 1));
		int guess = 1;
		
		while(guess <= 5)
		{
			System.out.print("Guess " + guess + ": ");
			int guessedNum;
			
			try
			{
				guessedNum = Integer.parseInt(in.nextLine());
				
				if(guessedNum == answer)
				{
					System.out.println("Congrats! You won! Thanks for playing.");
					return "So, now what?";
				}
				else
				{
					guess++;
				
					if(guessedNum > answer)
					{
						System.out.print("Too big. ");
					}
					else
					{
						System.out.print("Too small. ");
					}
				}
			}
			catch(Exception e)
			{
				System.out.print("That's not a valid number!");
			}
			
			System.out.println("Try again.");
		}
		
		return "Sorry, you're out of guesses. The number was " + answer + ". Thanks for playing.\nSo, now what?";
	}
	
	/**
	 * Pick a default response to use if nothing else fits.
	 *
	 * @return			 a non-committal string
	 * @version 2016.05.26
	 */
	private String getRandomResponse()
	{
		int randomIndex = (int)(Math.random() * RANDOM_RESPONSES.length);
		return RANDOM_RESPONSES[randomIndex];
	}
}
