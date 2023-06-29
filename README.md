#	**<div align="center">News Management System</div>**

      Java '20'    |    Gradle '8.1.1'     |    Docker    |    PostgreSQL     |     Redis     |    SpringBoot '3.1.0'

---

## **Description**
The Rest application allows you to work with news, comments and users in accordance with the their roles:

- **ADMIN**					-	*can perform CRUD operations with all entities* <small>(except for updating non-compliant)</small>
- **JOURNALIST**			-	*can add and change/delete only his own news*
- **SUBSCRIBER**			-	*can add and change/delete only his comments*
- **Unregistered users**	-	*can only view news and comments*

___

## **Installation**

1. *Clone the repository to your local machine:*

   >git clone https://github.com/AleeGen/news-management-system.git

2. *Navigate to the project directory:*

   >cd news-management-system

3. *Build a project:*

   >./gradlew build


4. *Install **[Docker](https://www.docker.com/products/docker-desktop/)***

5. *Create the containers using the following command(**Docker** must be running):*
   >docker-compose create

---

## **Start**

1. *Start the containers using the following command(Docker must be running):*

   >docker-compose up


2. *The containers will now be running and connected to the **news-network**.*

3. Get the documentation on the news management system by opening your web browser and clicking on the links:
   <br/>
   >**News-service:**	[http://localhost:8080/swagger-ui/index.html#](http://localhost:8080/swagger-ui/index.html#/)
   <br/>
   >**Users-service:**	[http://localhost:8081/swagger-ui/index.html#](http://localhost:8081/swagger-ui/index.html#/)

---

## **Endpoints**

#### News-service (*port 8080*):
<details style="border: 1px solid #ccc; border-radius: 4px; margin-bottom: 10px;">
  <summary style="padding: 10px; background-color: #f5f5f5; cursor: pointer;">Authentication</summary>

* POST
	- **``/auth/login``**		-	*Get authentication by login and password*
	- **``/auth/register``**	-	*Get authentication by registered user*
	- **``/auth/about_me``**	-	*Get information about the current authenticated user*
</details>

<details style="border: 1px solid #ccc; border-radius: 4px; margin-bottom: 10px;">
  <summary style="padding: 10px; background-color: #f5f5f5; cursor: pointer;">News</summary>

* GET
	- **``/news``**				-	*Get news according to pagination*
	- **``/news/{id}``**		-	*Get news by **id***
* POST
	- **``/news/``**			-	*Сreate news*
* PUT
	- **``/news/{id}``**		-	*Update news*
* PATCH
	- **``/news/{id}``**		-	*Specific news update*
* DELETE
	- **``/news/{id}``**		-	*Delete news by **id***
</details>

<details style="border: 1px solid #ccc; border-radius: 4px; margin-bottom: 10px;">
  <summary style="padding: 10px; background-color: #f5f5f5; cursor: pointer;">Comments</summary>

* GET
	- **``/comments``**				-	*Get comments according to pagination*
	- **``/comments/{id}``**		-	*Get comments by **id***
* POST
	- **``/comments/``**			-	*Сreate comments*
* PUT
	- **``/comments/{id}``**		-	*Update comments*
* PATCH
	- **``/comments/{id}``**		-	*Specific comments update*
* DELETE
	- **``/comments/{id}``**		-	*Delete comments by **id***
</details>

<details style="border: 1px solid #ccc; border-radius: 4px; margin-bottom: 10px;">
  <summary style="padding: 10px; background-color: #f5f5f5; cursor: pointer;">Users</summary>

* GET
	- **``/users``**			-	*Get comments according to pagination*
	- **``/users/{id}``**		-	*Get comments by **id***
* PUT
	- **``/users/{id}``**		-	*Update users*
* DELETE
	- **``/comments/{id}``**	-	*Delete users by **id***
</details>

#### Users-service (*port 8081*):
<details style="border: 1px solid #ccc; border-radius: 4px; margin-bottom: 10px;">
  <summary style="padding: 10px; background-color: #f5f5f5; cursor: pointer;">Authentication</summary>

* POST
	- **``/auth``**				-	*Get authentication by token*
	- **``/auth/login``**		-	*Get authentication by login and password*
	- **``/auth/register``**	-	*Get authentication by registered user*
	- **``/auth/about_me``**	-	*Get information about the current authenticated user*

</details>

<details style="border: 1px solid #ccc; border-radius: 4px; margin-bottom: 10px;">
  <summary style="padding: 10px; background-color: #f5f5f5; cursor: pointer;">Users</summary>

* GET
	- **``/users``**			-	*Get comments according to pagination*
	- **``/users/{id}``**		-	*Get comments by **id***
* PUT
	- **``/users/{id}``**		-	*Update users*
* DELETE
	- **``/comments/{id}``**	-	*Delete users by **id***
</details>
