# bigDataFindDiff
There is a file (size couple GBs) which contains numbers. All numbers except one are present even-times and one number present odd-times.
Create an effective algorithm to find this file.

Assumption:
1. Numbers in file are separated by EOL. One line - one number.
2. The numbers could be bigger then integer could contain. String used to represent number 
     for example number could be: "123345678901234567890123456789012345678901234567890"

Algorithm:
  Use HashSet to store current numbers which present odd-times.
1. Read file line by-line
2. If just read number is present in HashSet - remove it from HashSet
3. If just read number is not present in HashSet - add it
When all file is read then it could be:
1. HashSet contain one number -> return it
2. Otherwise throw exception that file does not contain such number or contain more then one

Problems:
1. File contains only different numbers (no numbers present twice) - in this case we should somehow understand that we read more then half of the file and number of numbers in HashSet more then it should be.
2. If HashSet will be too big to present in memory will it be casched into Disk Drive via JVM or it is necessary to perform it in Algorithm?
