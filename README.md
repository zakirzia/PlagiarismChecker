# Plagiarism Checker
Plagiarism Checker is used for detecting plagiarism and checking the originality of students' work.
It examines the uniqueness of content, helping to ensure that no part of a student's work has been copied from an external source.

This Plagiarism Checker compares text files against one another, 
providing an analysis of each file's content and a measure of similarity between each pair of files (uses Word Frequency and Phrase Matching metrics).
# Features
• Calculates Word Frequency for each file

• Calculates Phrase Matching % for each file based on user-settable threshold length

• Displays highlighted text to show where matched phrases were

• Ignores capitalisation, punctuation, whitespaces and quote marks

• Displays the results in HTML files

# Installation
1. Clone the repository - git clone https://github.com/zakirzia/PlagiarismChecker
2. Navigte to the repository - cd PlagiarismChecker
3. Compile Files - javac Main.java
5. Run the Main Class to start the Plagiarism Checker - java Main.java

# Usage
1. Set the threshold length for the Phrase Matching %
2. Set the number of files to check
3. Pass the file names/paths 
4. Then it will create an HTML page with Phrase Matching results and Word Frequency for all words in all files, 
you can click on each pair to view the word frequency, phrase matching and highlighted text.
