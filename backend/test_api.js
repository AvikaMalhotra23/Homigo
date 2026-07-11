const http = require('http');

const PORT = 5001;
const BASE_URL = `http://localhost:${PORT}/api`;

function request(path, method, body, token) {
  return new Promise((resolve, reject) => {
    const url = `${BASE_URL}${path}`;
    const payload = JSON.stringify(body || {});
    
    const options = {
      method: method,
      headers: {
        'Content-Type': 'application/json',
      }
    };
    
    if (token) {
      options.headers['Authorization'] = `Bearer ${token}`;
    }

    const req = http.request(url, options, (res) => {
      let data = '';
      res.on('data', (chunk) => {
        data += chunk;
      });
      res.on('end', () => {
        try {
          resolve({
            statusCode: res.statusCode,
            body: JSON.parse(data)
          });
        } catch (e) {
          resolve({
            statusCode: res.statusCode,
            body: data
          });
        }
      });
    });

    req.on('error', (err) => {
      reject(err);
    });

    if (body) {
      req.write(payload);
    }
    req.end();
  });
}

async function runTests() {
  console.log("=== STARTING HOMIGO BACKEND END-TO-END TESTS ===");

  try {
    // 1. Health check
    console.log("\nTesting /api/health...");
    const health = await request('/health', 'GET');
    console.log(`Status: ${health.statusCode}, Response:`, health.body);

    // 2. Register Boy
    console.log("\nRegistering a test boy...");
    const boyEmail = `kabir_${Date.now()}@lpu.in`;
    const registerBoy = await request('/auth/register', 'POST', {
      name: "Kabir Singh",
      email: boyEmail,
      password: "password123",
      gender: "male"
    });
    console.log(`Status: ${registerBoy.statusCode}, Response:`, registerBoy.body);
    const boyToken = registerBoy.body.token;

    // 3. Register Girl
    console.log("\nRegistering a test girl...");
    const girlEmail = `simran_${Date.now()}@lpu.in`;
    const registerGirl = await request('/auth/register', 'POST', {
      name: "Simran Kaur",
      email: girlEmail,
      password: "password123",
      gender: "female"
    });
    console.log(`Status: ${registerGirl.statusCode}, Response:`, registerGirl.body);
    const girlToken = registerGirl.body.token;

    // 4. Update Profile
    console.log("\nUpdating Kabir's roommate profile...");
    const profileRes = await request('/profile/update', 'POST', {
      college: "Lovely Professional University",
      hostel: "bh1",
      room_preference: "2-seater",
      budget_min: 3000,
      budget_max: 7000,
      sleep_schedule: "night_owl",
      smoking: "no",
      drinking: "no",
      food_preference: "veg",
      cleanliness: "high",
      pets: "no",
      guests: "rare",
      bio: "Quiet engineering student who studies late at night."
    }, boyToken);
    console.log(`Status: ${profileRes.statusCode}, Response:`, profileRes.body);

    // 5. Test AI Bio Generator
    console.log("\nTesting AI Bio Generator...");
    const aiBio = await request('/ai/generate-bio', 'POST', {
      input: "I like quiet places and play guitar sometimes"
    }, boyToken);
    console.log(`Status: ${aiBio.statusCode}, Response:`, aiBio.body);

    // 6. Test AI Chatbot
    console.log("\nTesting AI Chatbot...");
    const chatbot = await request('/ai/chatbot', 'POST', {
      message: "What questions should I ask before moving in?"
    }, boyToken);
    console.log(`Status: ${chatbot.statusCode}, Response:`, chatbot.body);

    // 7. Get Roommate Compatibility Matches
    console.log("\nFetching compatibility matches for Kabir (should match boys in LPU)...");
    const matches = await request('/matches', 'GET', null, boyToken);
    console.log(`Status: ${matches.statusCode}`);
    if (matches.body && matches.body.length > 0) {
      console.log(`Found ${matches.body.length} matches. Top Match:`);
      console.log(`Name: ${matches.body[0].user.name}, Overall Compatibility: ${matches.body[0].overallScore}%, Hostel: ${matches.body[0].user.hostel}`);
      console.log(`Breakdown:`, matches.body[0].breakdown);
    } else {
      console.log("No matches found!");
    }

    console.log("\n=== ALL MOCK TEST REQUESTS COMPLETED ===");
  } catch (err) {
    console.error("Test execution failed:", err.message);
  }
}

// Simple CLI check to run tests
runTests();
