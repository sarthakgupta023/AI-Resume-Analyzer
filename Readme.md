# ResumeIQ – AI Resume Analyzer

https://ai-resume-analyzer-green-six.vercel.app/

## Overview

- Built a full-stack AI-powered resume analysis platform that evaluates resumes against job descriptions.

- Generates ATS score, identifies missing skills, strengths, weaknesses, and provides actionable suggestions.

- Designed to simulate real recruiter-style evaluation for modern hiring workflows.

## Features

- Upload PDF resumes and extract text using Apache PDFBox

- Paste job description for role-based analysis

- AI-generated ATS score and structured feedback

- Missing skills detection based on job requirements

- Strengths and weaknesses analysis

- Recruiter-style summary generation

- Secure authentication using JWT

- Persistent analysis history for each user

- Responsive and clean SaaS-style UI

## Tech Stack

### Frontend

- React.js (Vite)

- Axios

- React Router DOM

- Modern CSS (custom design system)

### Backend

- Spring Boot

- Spring Security

- REST APIs

- JWT Authentication

- Apache PDFBox

### Database

- MongoDB Atlas

### AI Integration

- OpenAI API (gpt-4o-mini)

### Tools & Deployment

- Git, GitHub

- Vercel (Frontend)

- Render (Backend)

- Postman (API testing)

## Architecture

- Frontend communicates with backend via REST APIs

- Backend handles authentication, resume processing, and AI integration

- MongoDB stores users and analysis history

- JWT used for secure stateless authentication


