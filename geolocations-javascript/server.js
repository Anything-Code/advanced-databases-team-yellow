const path = require('path');
const express= require('express');
const dotenv = require('dotenv');
const cors = require('cors');

//load env vars
dotenv.config({path: './config/config.env'});

const app = express();

//Body parser 
app.use(express.json());

//Enable cors
app.use(cors());

//Routes 
app.use('/api/v1/locations', require('./routes/locations'));

const PORT = process.env.PORT || 5000;

app.listen(PORT, () => console.log(`server running in ${process.env.NODE_ENV} mode on port ${PORT}`)
);
