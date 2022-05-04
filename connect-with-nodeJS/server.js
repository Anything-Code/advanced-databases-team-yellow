const path = require('path');
const express = require('express');
const dotenv = require('dotenv');
const cors = require('cors');
// const connectDB = require('./config/db');

// load env vars
dotenv.config({path:'./config/config.env'});

const app= express();

// Body parser
app.use(express.json());

// Enable cors
app.use(cors());

// Routers
app.use('/api/v1/volunteers', require('./routes/volunteers'))

const PORT = process.env.PORT || 5000;

app.listen(PORT, () => console.log(`Server running in ${process.env.NODE_ENV} mode on port ${PORT}`));

