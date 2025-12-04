# PAW 2025b Grupo 7

* 61105: Causse, Juan Ignacio
* 62341: D'Intino, Rocio

---

# Content

* [What is OrgHub?](#what-is-orghub)
* [User Types](#user-types)
* [Credentials for Various User Types](#credentials-for-various-user-types)
* [Miscellaneous](#miscellaneous)

---

# What is OrgHub?

OrgHub is a centralized platform designed to connect students with student organizations within their educational institutions. It serves as a unified hub where students can discover, interact with, and participate in various organizational activities.

## Key Features

### For Students
- **Discover Organizations**: Browse and explore all student organizations available at your institution
- **Understand Organizations**: See organigrams for organizations
- **Subscribe to Organizations**: Follow organizations that interest you to stay updated with their latest content
- **View Content**: Access posts, events, and open positions from subscribed organizations in real-time
- **Participate in Events**: Enroll in events and activities organized by student organizations
- **Apply to Positions**: Apply to open positions within organizations
- **Interact**: Like posts, comment on content, and connect with other students who share similar interests

### For Organizations
- **Content Management**: Create and manage posts, events, and open positions
- **Event Management**: Organize events with detailed information, dates, locations, and enrollment management
- **Position Management**: Post open positions and manage applications from interested students

### For Institutions
- **Create new organizations**: Allow new organizations to publish their content in OrgHub

---

# User Types

## User
- **Base role** for all authenticated users
- **Permissions**:
  - Can like posts
  - Can comment on posts
  - Can apply to open positions
  - Can enroll in events
  - Can subscribe to organizations
  - Can view their content (liked posts, enrolled events, applied open positions)

## Organization Publisher
- Same permissions as a **User**, plus:
    - Can create posts, events and open positions

> **Important Notice**:
>
> This user type has not been fully implemented due to time limitations.
> The models that refer to `Role.PUBLISHER` are kept to allow for future
> implementations of this feature.

## Organization Manager
- Same permissions as a **User**, plus:
  - Can create posts, events and open positions
  - Can edit and delete posts, events and open positions
  - Can delete comments

## Institution Administrator
- Same permissions as a **User**, plus:
  - Can create organizations for their institution

## System Administrator
- **Type**: Backoffice role
- **Permissions**:
    - Can create Institutions
    - Full system administration access


---

# Credentials for Various User Types

## ITBA Institution normal User
* **Email**: `jcausse@itba.edu.ar`
* **Password**: `jcausse@itba.edu.ar` (same as email)
* 
## ITBA Institution Administrator
* **Email**: `rdintino@itba.edu.ar`
* **Password**: `rdintino@itba.edu.ar` (same as email)

## System Administrator
* **Email**: `sysadmin@orghub.com`
* **Password**: `&Sgo7TFUP!w015qzWR4*`

---

# Miscellaneous

## OrgHub Gmail credentials
* **Email**: `orghub.paw@gmail.com`
* **Password**: `D67Dt*^8#v*&32s`
* **App Password**: `izfn tatm rcuh knma`
