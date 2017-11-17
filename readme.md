Floor Plan Storage Manager
=======================================

**This project provides below three APIs**
1.	Create project
> GET method
> /projects/add?projectName=Sam

2.	Get all projects
> GET method
> /projects

3.	Upload floor plans to a project
> POST method (form-data)
> /floorplans/upload
> body example:
 
> Parameters:
 - file : file to be attached
 - id : project id, fetched from the UI
 - projectName : name of the project associated with this file upload
 - displayName : file name, which should be extracted from the uploaded file name or given name by an user
 - filetype : type of file (pdf or jpg). Only pdf and jpg uploads are supported. Should be fetched from the uploaded file
 - replaceFlag : if file already exists, would user like to replace it?

**Components used in the project:**
    - Spring boot (Back-end framework)
    - JPA (Database access)
    - AWS S3 (Regular for now) //TODO -> use RRS for thumbnails and large pngs, also use –––––––rules
    - AWS RDS (MySQL)
    - AWS Elastic Beanstalk (EC2, Load Balancer and Auto Scaling groups) to host SpringBoot application
    - Secret-key authentication (additional security while making api calls)

**Database schema:**
1.	Projects table (Project Id,	Project Name)

2.	Floor Plans table (Id, Project Id, Display Name, File Type, File Name(with extension))


**Edge cases handled in the back-end:**
- Project exists before file upload
- File already exists and if yes, user would like to replace it?
- File type is .pdf or .jpg
- do not add new record to database while replacing the file on S3

**Examples of API calls using (Postman)**
1.	Get projects
 
2.	Create project
-> Create Project
 

-> Project already exist
 

3.	Upload floor plans to a project
-> Upload a file
 

 
-> Try uploading same file with replaceFlag false
 

-> Try uploading same file with replaceFlag true
 

 

-> Try to upload a file on non-existing Project by manipulating params from Front-end (here postman)
 

**Database**
Database connection to AWS RDS (to use locally):
 

Create table script is stored within resources folder with name InitialScript.sql



**AWS Elastic Beanstalk (App deployment using .jar and scaling)**
**NOTE** : below part may not be active once instance is stopped or removed. Though it is easy to reconfigure it with your own RDS and S3 connection.
Reference -> https://aws.amazon.com/blogs/devops/deploying-a-spring-boot-application-on-aws-using-aws-elastic-beanstalk/

Auto Scalling:
 
 

Load Balancing:
  

 


