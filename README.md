__Text Search with Context__

This project creates a simple text search with context.

The input file is tokenized with words being alphanumerics (including apostraphes) and then the
remaining is considered punctuation.  Searching is case insensitive and returns words around the target to provide
context.  If this extends to beyond the file, the output will be trimmed by the file bounds.

__How This Works__

A regex parser goes through and splits each chunk of text that matches the definition of a word and those that don't.
These tokens are added to a list to make them easily indexed for retrieval later.  On top of this, valid words are added
to a HashMap.  This map uses the lowercase version of the token as the key and value is an array of all indices where
the token was found.  This allows us to easily return a list of all locations of a particular word in a given text.
During query, the HashMap is used to pull a list of all indices.  For each index, a start and end index are calculated.
These are mearly the index of the query with the context count on either side.  Then a string builder takes the tokens
from the token list and appends them together between the two indices we previously calculated.  This gives us our
word search plus context.