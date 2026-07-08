// Central college + hostel registry.
// Exposes major Indian universities with their custom gender-specific hostels.

const COLLEGES = [
  {
    id: 'lpu',
    name: 'Lovely Professional University',
    city: 'Phagwara, Punjab',
    hostels: {
      male: Array.from({ length: 10 }, (_, i) => ({ id: `bh${i + 1}`, label: `BH ${i + 1}` })),
      female: Array.from({ length: 9 }, (_, i) => ({ id: `gh${i + 1}`, label: `GH ${i + 1}` })),
    },
  },
  {
    id: 'thapar',
    name: 'Thapar Institute of Engineering & Technology',
    city: 'Patiala, Punjab',
    hostels: {
      male: [
        { id: 'hostel_a', label: 'Hostel A' },
        { id: 'hostel_b', label: 'Hostel B' },
        { id: 'hostel_c', label: 'Hostel C' },
        { id: 'hostel_j', label: 'Hostel J' },
        { id: 'hostel_m', label: 'Hostel M' }
      ],
      female: [
        { id: 'hostel_e', label: 'Hostel E' },
        { id: 'hostel_g', label: 'Hostel G' },
        { id: 'hostel_i', label: 'Hostel I' },
        { id: 'hostel_n', label: 'Hostel N' }
      ],
    },
  },
  {
    id: 'iitd',
    name: 'Indian Institute of Technology, Delhi (IITD)',
    city: 'Hauz Khas, New Delhi',
    hostels: {
      male: [
        { id: 'aravali', label: 'Aravali Hostel' },
        { id: 'jwalamukhi', label: 'Jwalamukhi Hostel' },
        { id: 'karakoram', label: 'Karakoram Hostel' },
        { id: 'nilgiri', label: 'Nilgiri Hostel' },
        { id: 'kumaon', label: 'Kumaon Hostel' },
        { id: 'girnar', label: 'Girnar Hostel' }
      ],
      female: [
        { id: 'kailash', label: 'Kailash Hostel' },
        { id: 'shivalik', label: 'Shivalik Hostel' },
        { id: 'himadri', label: 'Himadri Hostel' }
      ],
    },
  },
  {
    id: 'du',
    name: 'Delhi University (DU)',
    city: 'New Delhi, Delhi',
    hostels: {
      male: [
        { id: 'gwyer_hall', label: 'Gwyer Hall' },
        { id: 'jubilee_hall', label: 'Jubilee Hall' },
        { id: 'vkrv_rao', label: 'VKRV Rao Hostel' }
      ],
      female: [
        { id: 'uhw', label: 'University Hostel for Women' },
        { id: 'nivedita', label: 'Nivedita House' },
        { id: 'ughw', label: 'Undergraduate Hostel for Women' }
      ],
    },
  }
];

function getCollegeById(id) {
  return COLLEGES.find((c) => c.id === id) || null;
}

module.exports = { COLLEGES, getCollegeById };
