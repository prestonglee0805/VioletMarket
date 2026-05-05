# VioletMarket
**Object Oriented Programming Final Project — Spring 2026**

May 2026

---

## Group
**Preston Lee** — [prestonglee0805](https://github.com/prestonglee0805)

**Tristan Zhen** — [GitHub](https://github.com/tristanz9)

---

## About
VioletMarket is an NYU-only peer-to-peer CLI marketplace built in Java. Registration requires a verified `@nyu.edu` email address, ensuring a trusted student-to-student community. Students can list, browse, search, and purchase items using an in-app wallet system.

---

## Running the Project

Clone the repository:

```
git clone https://github.com/prestonglee0805/OOP-final.git
```

With the current working directory as the project root, run:

```
make run
```

---

## Default Credentials

**Admin login:**
- Email: `admin@nyu.edu`
- Password: `admin123`

**Pre-loaded student accounts** (password for all: `password123`):

| Name         | Email               |
|--------------|---------------------|
| Preston Lee  |  pgl8973@nyu.edu    |
| Tristan Zhen |  tcz2003@nyu.edu    |

Student credentials can be updated by editing `data/users.json`.
Preloaded listings can be updated by editing `data/listings.json`. 
Both pre-loaded credentials contain different wallet balance amounts.

---

## Project Structure

```
OOP-final/
├── src/
│   ├── cli/          # CLI handler
│   ├── model/        # Core classes
│   ├── enums/        # ItemCategory, ItemCondition, ListingStatus, PickupZone
│   ├── interfaces/   # Searchable, Moderatable, Ratable
│   └── exception/    # InvalidEmailException, InsufficientFundsException
├── data/
│   ├── users.json    # Pre-loaded student accounts
│   └── listings.json # Pre-loaded marketplace listings
└── Makefile
```
