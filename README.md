# Sysnormal Database Base Entities Library

---

## 🚀 Purpose

This library centralizes and standardizes JPA entity classes used across multiple systems.  
Instead of duplicating entity definitions, applications simply import this library.

Benefits include:

- Consistency across services
- Single source of truth for table mappings
- Reduced maintenance and code duplication
- Simplified integration databases

---

## 🛠 Technologies Used

- **Java 21+**
- **Jakarta Persistence API (JPA)**
- **Hibernate ORM**
- **Spring Data (optional / provided scope)**
- **Maven** for dependency management

---

## 📥 Installation (Maven)

Add this dependency to your application’s `pom.xml`:

```xml
<dependency>
    <groupId>com.sysnormal.libs.db.entities</groupId>
    <artifactId>base-entities</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

---

## 🔧 Usage Example
Importing an entity
````java
import com.sysnormal.libs.db.entities.base_entities.agents.Agent;

@Service
public class AgentService {

    @PersistenceContext
    private EntityManager em;

    public Agent find(Long id) {
        return em.find(Agent.class, id);
    }
}

````
Repository Example (Spring Data)
````java
public interface AgentRepository extends JpaRepository<Agent, Long> {}
````
---

## 📚 How to Clone
````bash
git clone https://github.com/sysnormal1/java-db-base-entities-library.git

````

------------------------------------------------------------------------

## 📜 License

MIT License --- free for personal and commercial use.

------------------------------------------------------------------------

## 👤 Author

**Alencar Velozo**  
GitHub: [@aalencarvz1](https://github.com/aalencarvz1)
Organization: [@sysnormal1](https://github.com/sysnormal1)
Software Engineer --- JPA, Hibernate, Spring & Database Specialist

------------------------------------------------------------------------
