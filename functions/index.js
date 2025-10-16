const functions = require("firebase-functions/v1");
const admin = require("firebase-admin");
const nodemailer = require("nodemailer");

// Firebase Admin initialize
admin.initializeApp({
  credential: admin.credential.applicationDefault(),
  databaseURL: "https://smartcampusmanagement-cce20-default-rtdb.asia-southeast1.firebasedatabase.app/"
});

// Nodemailer setup
const transporter = nodemailer.createTransport({
  service: "gmail",
  auth: {
    user: "jahidulh829@gmail.com",   // তোমার Gmail
    pass: "naaxquhupywjsdup"         // Gmail App Password (space বাদ দিতে হবে)
  }
});

// Trigger: যখন নতুন student add হবে
exports.sendStudentEmail = functions
  .region("asia-southeast1")
  .database.ref("/users/{userId}")
  .onCreate(async (snapshot, context) => {
    const user = snapshot.val();

    const mailOptions = {
      from: "jahidulh829@gmail.com",
      to: user.email,
      subject: "🎓 ITSC Smart Campus Login Info",
      text: `Dear ${user.name},

Your student account has been created successfully!

User Id: ${user.user_id}
Password: ${user.password}

Login at your student portal.

Regards,
ITSC Smart Campus Management System`
    };

    try {
      const info = await transporter.sendMail(mailOptions);
      console.log("Email sent:", info.response);
    } catch (error) {
      console.error("Error sending email:", error);
    }
  });
