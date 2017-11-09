from collections import namedtuple
import re

PointOfView = namedtuple('PointOfView', ['subjectPronoun', 'objectPronoun', 'beConjugation', 'standardConjugation'])

FIRST_SINGULAR_POV				= PointOfView(subjectPronoun = 'i', objectPronoun = 'me', beConjugation = 'am', standardConjugation = '')
FIRST_PLURAL_POV				= PointOfView(subjectPronoun = 'we', objectPronoun = 'us', beConjugation = 'are', standardConjugation = '')
SECOND_SINGULAR_POV				= PointOfView(subjectPronoun = 'you', objectPronoun = 'you', beConjugation = 'are', standardConjugation = '')
SECOND_PLURAL_POV 				= SECOND_SINGULAR_POV
THIRD_MASCULINE_SINGULAR_POV 	= PointOfView(subjectPronoun = 'he', objectPronoun = 'him', beConjugation = 'is', standardConjugation = 's')
THIRD_FEMININE_SINGULAR_POV 	= PointOfView(subjectPronoun = 'she', objectPronoun = 'her', beConjugation = 'is', standardConjugation = 's')
THIRD_NEUTER_SINGULAR_POV 		= PointOfView(subjectPronoun = 'it', objectPronoun = 'it', beConjugation = 'is', standardConjugation = 's')
THIRD_PLURAL_POV 				= PointOfView(subjectPronoun = 'they', objectPronoun = 'them', beConjugation = 'are', standardConjugation = '')

MODAL_AUXILIARIES  				= { 'can', 'could', 'may', 'might', 'must', 'shall', 'should', 'will', 'would' }
POINTS_OF_VIEW 					= { FIRST_SINGULAR_POV, FIRST_PLURAL_POV, SECOND_SINGULAR_POV, SECOND_PLURAL_POV,
									THIRD_MASCULINE_SINGULAR_POV, THIRD_FEMININE_SINGULAR_POV, THIRD_NEUTER_SINGULAR_POV, THIRD_PLURAL_POV }
QUESTION_WORDS     				= { 'who', 'what', 'when', 'where', 'how', 'which' }

BE_VERBS = { pov.beConjugation for pov in POINTS_OF_VIEW }
SUBJECT_PRONOUNS = { pov.subjectPronoun for pov in POINTS_OF_VIEW }

def isQuestion(statement: str) -> bool:
	'Determines if the given statement is a grammatically valid question.'
	
	isWordAfter = lambda word, wordAfter: _getWordAfter(statement, word) == wordAfter
	
	MODALS_AND_VERBS = BE_VERBS | MODAL_AUXILIARIES
	
	containsPronounAfterKeyword = any({ isWordAfter(word, pronoun) for word in MODALS_AND_VERBS for pronoun in SUBJECT_PRONOUNS })
	containsQuestionWord = any({ containsWord(statement, word) for word in QUESTION_WORDS })
	containsQuestionMark = '?' in statement
	
	return any((containsPronounAfterKeyword, containsQuestionWord, containsQuestionMark))
	
def removeContractions(statement: str) -> str:
	'Returns a copy of the statement with all contractions expanded.'
	
	removedNots = statement.replace("n't", ' not').replace('can not', 'cannot')
	removedWills = removedNots.replace("'ll", ' will')
	removedIss = removedWills.replace("'s", ' is')
	removedAres = removedIss.replace("'re", ' are')
	
	return removedAres

def invertPointOfView(statement: str) -> str:
	'Returns a copy of the statement with the first and second points of view switched.'
	
	verb = getVerb(statement)
	subject, predicate = statement.split(verb, 1)
	subjectWordList, predicateWordList = _splitToWordList(subject), _splitToWordList(predicate)
	
	invertedSubjectWordList = [ _getInvertedSubject(word) for word in subjectWordList ]
	inveredPredicateWordList = [ _getInvertedObject(word) for word in predicateWordList ]
	
	wordList = invertedSubjectWordList + inveredPredicateWordList
	
	# correct the be verbs after swapping pronouns
	
	for index, word in enumerate(wordList):
		if word not in BE_VERBS:
			continue
		
		if FIRST_SINGULAR_POV.subjectPronoun in { _wordBefore(statement, word), _wordAfter(statement, word) }: # verb comes before in questions, after in statements
			wordList[index] = FIRST_SINGULAR_POV.beVerb
		elif SECOND_SINGULAR_POV.subjectPronoun in { _wordBefore(statement, word), _wordAfter(statement, word) }:
			wordList[index] = SECOND_SINGULAR_POV.beVerb
	
	return wordList

def indexOfWord(statement: str, word: str, start: int = 0) -> int:
	'Returns the index of the given word in the statement.'
	
	if start >= len(statement) or start < 0:
		return -1
	
	paddedStatement = f' {statement} ' # add spaces to make it easy to check if boundary characters are non-alpha
	offsetPosition = paddedStatement.find(word, start + 1)
	
	# the word is just not found
	if offsetPosition == -1:
		return -1
	
	# the word was actually found, and it's not just a substring of another word
	if (not paddedStatement[offsetPosition - 1].isalpha()) and (not paddedStatement[offsetPosition + len(word)].isalpha()):
		return offsetPosition - 1 # undo the added space
	
	# the word was found, but since it was just a substring of another word, let's keep looking
	return indexOfWord(statement, word, offsetPosition - 1 + 1)

def containsWord(statement: str, word: str) -> bool:
	'Determines if the statement contains the given word.'
	
	return indexOfWord(statement, word) >= 0

def getModalAuxiliary(statement: str) -> str:
	'Returns the first modal auxiliary in the given statement.'
	
	return _getFirstWordFromSet(statement, MODAL_AUXILIARIES)

def containsModalAuxiliary(statement: str) -> bool:
	'Determines if the given statement contains a modal auxiliary.'
	
	return getModalAuxiliary(statement) != None

def getQuestionWord(statement: str) -> str:
	'Returns the first question word in the given statement.'
	
	return _getFirstWordFromSet(statement, QUESTION_WORDS)

def containsQuestionWord(statement: str) -> bool:
	'Determines if the given statement contains a question word.'
	
	return getQuestionWord(statement) != None

def getSubjectPronoun(statement: str) -> str:
	'Returns the first subject pronoun in the given statement.'
	
	return _getFirstWordFromSet(statement, SUBJECT_PRONOUNS)

def containsSubjectPronoun(statement: str) -> bool:
	'Determines if the given statement contains a subject pronoun.'
	
	return getSubjectPronoun(statement) != None

def getObjectPronoun(statement: str) -> str:
	'Returns the first object pronoun in the given statement.'
	
	OBJECT_PRONOUNS = { pov.objectPronoun for pov in POINTS_OF_VIEW }
	return _getFirstWordFromSet(statement, OBJECT_PRONOUNS)

def containsObjectWord(statement: str) -> bool:
	'Determines if the given statement contains a object pronoun.'
	
	return getObjectPronoun(statement) != None

def getBeVerb(statement: str) -> str:
	'Returns the first be verb in the given statement.'
	
	return _getFirstWordFromSet(statement, BE_VERBS)

def containsBeVerb(statement: str) -> bool:
	'Determines if the given statement contains a be verb.'
	
	return getBeVerb(statement) != None

def _getFirstWordFromSet(statement: str, words: set, startsWithOnly: bool = False) -> str:
	'Returns the first word in the statement that also exists in the given set of words.'
	
	splitStatement = _splitToWordList(statement)
	
	isStartOfStatementWord = lambda keyword: any({ word.startswith(keyword) for word in splitStatement })
	isInStatement = lambda keyword: keyword in splitStatement
	wordMatches = lambda keyword:  isStartOfStatementWord(keyword) if startsWithOnly else isInStatement(keyword)
	
	matches = [ word for word in words if wordMatches(word) ]
	
	if matches == []:
		return None
	else:
		return matches[0]

def _getWordAfter(statement: str, word: str) -> str:
	'Returns the word that follows the given word in the statement.'
	
	wordList = _splitToWordList(statement)
	indexOfWord = -1
	
	try:
		indexOfWord = wordList.index(word)
	except ValueError:
		pass
	
	if indexOfWord < 0 or indexOfWord >= len(wordList):
		return ''
	else:
		return wordList[indexOfWord + 1]
	
def _getWordBefore(statement: str, word: str) -> str:
	'Returns the word that is followed by the given word in the statement.'
	
	wordList = _splitToWordList(statement)
	indexOfWord = -1
	
	try:
		indexOfWord = wordList.index(word)
	except ValueError:
		pass
	
	if indexOfWord <= 0:
		return ''
	else:
		return wordList[indexOfWord - 1]

def _getInvertedSubject(subject: str) -> str:
	'Returns the inverse of the given subject pronoun.'
	
	if subject == FIRST_SINGULAR_POV.subjectPronoun:
		return SECOND_SINGULAR_POV.subjectPronoun
	elif subject == SECOND_SINGULAR_POV.subjectPronoun:
		return FIRST_SINGULAR_POV.subjectPronoun
	elif subject == FIRST_PLURAL_POV.subjectPronoun:
		return SECOND_PLURAL_POV.subjectPronoun
	else:
		return subject

def _getInvertedObject(obj: str) -> str:
	'Returns the inverse of the given object pronoun.'
	
	if obj == FIRST_SINGULAR_POV.objectPronoun:
		return SECOND_SINGULAR_POV.objectPronoun
	elif obj == SECOND_SINGULAR_POV.objectPronoun:
		return FIRST_SINGULAR_POV.objectPronoun
	elif obj == FIRST_PLURAL_POV.objectPronoun:
		return SECOND_PLURAL_POV.objectPronoun
	else:
		return obj

def _splitToWordList(statement: str) -> [str]:
	'Returns an ordered list of words contained in the given statement.'
	
	splitStatement = re.split('\\W', statement)
	isNotEmptyString = lambda string: string != ''
	return list(filter(isNotEmptyString, splitStatement))