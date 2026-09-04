## UI test plan

Build command: `./gradlew clean classes`

Program command: `java -cp build/classes/java/main dude.Dude`

Each expected output is the complete stdout session. The runner uses Java 25
and the non-interactive default terminal width of 60 characters.

JUnit test command: `./gradlew test`

JUnit coverage includes date parsing and validation in `TaskDateTest`, and
case-insensitive ordered task search in `TaskListTest`.

## Test case: typed dates and date query

Aim: Verify that date-only and date-time values are parsed, formatted, persisted, and found by `on`.

### Inputs

```text
deadline return book /by 2019-12-02 1800
event project /from 2019-12-01 /to 2019-12-03
on 2019-12-02
list
bye
```

### Expected output

```text
────────────────────────────────────────────────────────────
██████╗  ██╗   ██╗ ██████╗  ███████╗
██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝
██║  ██║ ██║   ██║ ██║  ██║ █████╗
██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝
██████╔╝ ╚██████╔╝╚██████╔╝ ███████╗
╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝
Hello! I'm DUDE.
Dates can be represented in this format: yyyy-MM-dd.
To include a time, use this format: yyyy-MM-dd HHmm.
What can I do for you?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Got it. I've added this task:
  [D][ ] return book (by: Dec 02 2019 18:00)
Now you have 1 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Got it. I've added this task:
  [E][ ] project (from: Dec 01 2019 to: Dec 03 2019)
Now you have 2 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Tasks occurring on 2019-12-02:
1.[D][ ] return book (by: Dec 02 2019 18:00)
2.[E][ ] project (from: Dec 01 2019 to: Dec 03 2019)
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here are the tasks in your list:
1.[D][ ] return book (by: Dec 02 2019 18:00)
2.[E][ ] project (from: Dec 01 2019 to: Dec 03 2019)
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Bye. Hope to see you again soon!
────────────────────────────────────────────────────────────
```

## Test case: invalid temporal input

Aim: Verify that invalid calendar dates and reversed event ranges are rejected without adding tasks.

### Inputs

```text
deadline invalid /by 2019-02-29
event reversed /from 2019-12-03 /to 2019-12-02
on 2019-02-30
list
bye
```

### Expected output

```text
────────────────────────────────────────────────────────────
██████╗  ██╗   ██╗ ██████╗  ███████╗
██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝
██║  ██║ ██║   ██║ ██║  ██║ █████╗
██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝
██████╔╝ ╚██████╔╝╚██████╔╝ ███████╗
╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝
Hello! I'm DUDE.
Dates can be represented in this format: yyyy-MM-dd.
To include a time, use this format: yyyy-MM-dd HHmm.
What can I do for you?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Error: invalid by "2019-02-29" for deadline.
Expected: yyyy-MM-dd or yyyy-MM-dd HHmm.
Usage: deadline <description> /by <yyyy-MM-dd [HHmm]>
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Error: invalid to "2019-12-02" for event.
Expected: a date on or after the start date.
Usage: event <description> /from <yyyy-MM-dd [HHmm]> /to <yyyy-MM-dd [HHmm]>
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Error: invalid date "2019-02-30" for on.
Expected: yyyy-MM-dd.
Usage: on <yyyy-MM-dd>
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here are the tasks in your list:
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Bye. Hope to see you again soon!
────────────────────────────────────────────────────────────
```

## Test case: corrupted temporal records

Aim: Verify that malformed JSONL records and legacy free-form temporal values remain visible with their raw content.

### Data file before startup

```text
not json
{"type":"D","done":false,"description":"old","by":"Sunday"}
```

### Inputs

```text
list
bye
```

### Expected output

```text
────────────────────────────────────────────────────────────
██████╗  ██╗   ██╗ ██████╗  ███████╗
██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝
██║  ██║ ██║   ██║ ██║  ██║ █████╗
██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝
██████╔╝ ╚██████╔╝╚██████╔╝ ███████╗
╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝
Hello! I'm DUDE.
Dates can be represented in this format: yyyy-MM-dd.
To include a time, use this format: yyyy-MM-dd HHmm.
What can I do for you?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here are the tasks in your list:
1.[C][ ] [Corrupted: not json]
2.[C][ ] [Corrupted: {"type":"D","done":false,"description":"old","by":"Sunday"}]
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Bye. Hope to see you again soon!
────────────────────────────────────────────────────────────
```
## Test case: find tasks by keyword

Aim: Verify that find performs case-insensitive description matching, handles no matches, and rejects a missing keyword.

### Inputs

```text
todo read book
deadline return book /by 2019-12-02
todo buy milk
find BOOK
find train
find
bye
```

### Expected output

```text
────────────────────────────────────────────────────────────
██████╗  ██╗   ██╗ ██████╗  ███████╗
██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝
██║  ██║ ██║   ██║ ██║  ██║ █████╗
██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝
██████╔╝ ╚██████╔╝╚██████╔╝ ███████╗
╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝
Hello! I'm DUDE.
Dates can be represented in this format: yyyy-MM-dd.
To include a time, use this format: yyyy-MM-dd HHmm.
What can I do for you?
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Got it. I've added this task:
  [D][ ] return book (by: Dec 02 2019)
Now you have 2 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Got it. I've added this task:
  [T][ ] buy milk
Now you have 3 tasks in the list.
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Dec 02 2019)
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here are the matching tasks in your list:
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Error: invalid keyword <missing> for find.
Expected: a non-blank keyword.
Usage: find <keyword>
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Bye. Hope to see you again soon!
────────────────────────────────────────────────────────────
```
## Latest test session

### typed dates and date query
Console input:
```text
deadline return book /by 2019-12-02 1800
event project /from 2019-12-01 /to 2019-12-03
on 2019-12-02
list
bye
```
Console output:
```text
────────────────────────────────────────────────────────────
██████╗  ██╗   ██╗ ██████╗  ███████╗                        
██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝                        
██║  ██║ ██║   ██║ ██║  ██║ █████╗                          
██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝                          
██████╔╝ ╚██████╔╝╚██████╔╝ ███████╗                        
╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝                        
Hello! I'm DUDE.                                            
Dates can be represented in this format: yyyy-MM-dd.        
To include a time, use this format: yyyy-MM-dd HHmm.        
What can I do for you?                                      
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Got it. I've added this task:                               
  [D][ ] return book (by: Dec 02 2019 18:00)                
Now you have 1 tasks in the list.                           
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Got it. I've added this task:                               
  [E][ ] project (from: Dec 01 2019 to: Dec 03 2019)        
Now you have 2 tasks in the list.                           
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Tasks occurring on 2019-12-02:                              
1.[D][ ] return book (by: Dec 02 2019 18:00)                
2.[E][ ] project (from: Dec 01 2019 to: Dec 03 2019)        
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here are the tasks in your list:                            
1.[D][ ] return book (by: Dec 02 2019 18:00)                
2.[E][ ] project (from: Dec 01 2019 to: Dec 03 2019)        
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Bye. Hope to see you again soon!                            
────────────────────────────────────────────────────────────
```

### invalid temporal input
Console input:
```text
deadline invalid /by 2019-02-29
event reversed /from 2019-12-03 /to 2019-12-02
on 2019-02-30
list
bye
```
Console output:
```text
────────────────────────────────────────────────────────────
██████╗  ██╗   ██╗ ██████╗  ███████╗                        
██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝                        
██║  ██║ ██║   ██║ ██║  ██║ █████╗                          
██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝                          
██████╔╝ ╚██████╔╝╚██████╔╝ ███████╗                        
╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝                        
Hello! I'm DUDE.                                            
Dates can be represented in this format: yyyy-MM-dd.        
To include a time, use this format: yyyy-MM-dd HHmm.        
What can I do for you?                                      
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Error: invalid by "2019-02-29" for deadline.                
Expected: yyyy-MM-dd or yyyy-MM-dd HHmm.                    
Usage: deadline <description> /by [31m<yyyy-MM-dd [HHmm]>[0m
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Error: invalid to "2019-12-02" for event.                   
Expected: a date on or after the start date.                
Usage: event <description> /from [31m<yyyy-MM-dd [HHmm]>[0m /to [31m<yyyy-MM-dd [HHmm]>[0m
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Error: invalid date "2019-02-30" for on.                    
Expected: yyyy-MM-dd.                                       
Usage: on [31m<yyyy-MM-dd>[0m                             
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here are the tasks in your list:                            
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Bye. Hope to see you again soon!                            
────────────────────────────────────────────────────────────
```

### corrupted temporal records
Console input:
```text
list
bye
```
Console output:
```text
────────────────────────────────────────────────────────────
██████╗  ██╗   ██╗ ██████╗  ███████╗                        
██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝                        
██║  ██║ ██║   ██║ ██║  ██║ █████╗                          
██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝                          
██████╔╝ ╚██████╔╝╚██████╔╝ ███████╗                        
╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝                        
Hello! I'm DUDE.                                            
Dates can be represented in this format: yyyy-MM-dd.        
To include a time, use this format: yyyy-MM-dd HHmm.        
What can I do for you?                                      
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here are the tasks in your list:                            
1.[C][ ] [Corrupted: not json]                              
2.[C][ ] [Corrupted: {"type":"D","done":false,"description":"old","by":"Sunday"}]
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Bye. Hope to see you again soon!                            
────────────────────────────────────────────────────────────
```

### find tasks by keyword
Console input:
```text
todo read book
deadline return book /by 2019-12-02
todo buy milk
find BOOK
find train
find
bye
```
Console output:
```text
────────────────────────────────────────────────────────────
██████╗  ██╗   ██╗ ██████╗  ███████╗                        
██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝                        
██║  ██║ ██║   ██║ ██║  ██║ █████╗                          
██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝                          
██████╔╝ ╚██████╔╝╚██████╔╝ ███████╗                        
╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝                        
Hello! I'm DUDE.                                            
Dates can be represented in this format: yyyy-MM-dd.        
To include a time, use this format: yyyy-MM-dd HHmm.        
What can I do for you?                                      
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Got it. I've added this task:                               
  [T][ ] read book                                          
Now you have 1 tasks in the list.                           
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Got it. I've added this task:                               
  [D][ ] return book (by: Dec 02 2019)                      
Now you have 2 tasks in the list.                           
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Got it. I've added this task:                               
  [T][ ] buy milk                                           
Now you have 3 tasks in the list.                           
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here are the matching tasks in your list:                   
1.[T][ ] read book                                          
2.[D][ ] return book (by: Dec 02 2019)                      
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Here are the matching tasks in your list:                   
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Error: invalid keyword <missing> for find.                  
Expected: a non-blank keyword.                              
Usage: find [31m<keyword>[0m                              
────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────
Bye. Hope to see you again soon!                            
────────────────────────────────────────────────────────────
```
