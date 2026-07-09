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
  },
  {
    id: 'bits_pilani',
    name: 'BITS Pilani, Pilani',
    city: 'Pilani, Rajasthan',
    hostels: {
      male: Array.from({ length: 12 }, (_, i) => ({ id: `bh_${i + 1}`, label: `Vyas/Gandhi/Bhagirath BH ${i + 1}` })),
      female: Array.from({ length: 4 }, (_, i) => ({ id: `gh_${i + 1}`, label: `Meera Hall GH ${i + 1}` })),
    }
  },
  {
    id: 'bits_hyd',
    name: 'Birla Institute of Technology and Science - [BITS], Hyderabad',
    city: 'Hyderabad, Telangana',
    hostels: {
      male: Array.from({ length: 9 }, (_, i) => ({ id: `bh_${i + 1}`, label: `Valmiki/Vyas BH ${i + 1}` })),
      female: Array.from({ length: 3 }, (_, i) => ({ id: `gh_${i + 1}`, label: `Meera GH ${i + 1}` })),
    }
  },
  {
    id: 'iiit_allahabad',
    name: 'Indian Institute of Information Technology - [IIIT], Allahabad',
    city: 'Allahabad, Uttar Pradesh',
    hostels: {
      male: Array.from({ length: 5 }, (_, i) => ({ id: `bh_${i + 1}`, label: `Boys Hostel ${i + 1}` })),
      female: Array.from({ length: 3 }, (_, i) => ({ id: `gh_${i + 1}`, label: `Girls Hostel ${i + 1}` })),
    }
  },
  {
    id: 'bits_goa',
    name: 'Birla Institute of Technology and Science - [BITS], South Goa',
    city: 'South Goa, Goa',
    hostels: {
      male: Array.from({ length: 9 }, (_, i) => ({ id: `bh_${i + 1}`, label: `AH ${i + 1}` })),
      female: Array.from({ length: 2 }, (_, i) => ({ id: `gh_${i + 1}`, label: `CH ${i + 1}` })),
    }
  },
  {
    id: 'nit_kkr',
    name: 'National Institute of Technology - [NIT], Kurukshetra',
    city: 'Kurukshetra, Haryana',
    hostels: {
      male: Array.from({ length: 10 }, (_, i) => ({ id: `bh_${i + 1}`, label: `Hostel ${i + 1}` })),
      female: Array.from({ length: 4 }, (_, i) => ({ id: `gh_${i + 1}`, label: `Kalpana Chawla GH ${i + 1}` })),
    }
  },
  {
    id: 'lnmiit_jaipur',
    name: 'The LNM Institute of Information Technology - [LNMIIT], Jaipur',
    city: 'Jaipur, Rajasthan',
    hostels: {
      male: Array.from({ length: 4 }, (_, i) => ({ id: `bh_${i + 1}`, label: `BH ${i + 1}` })),
      female: Array.from({ length: 2 }, (_, i) => ({ id: `gh_${i + 1}`, label: `GH ${i + 1}` })),
    }
  },
  {
    id: 'daiict_gandhinagar',
    name: 'Dhirubhai Ambani University, Gandhinagar',
    city: 'Gandhinagar, Gujarat',
    hostels: {
      male: Array.from({ length: 3 }, (_, i) => ({ id: `bh_${i + 1}`, label: `Boys Hostel Block ${i + 1}` })),
      female: Array.from({ length: 2 }, (_, i) => ({ id: `gh_${i + 1}`, label: `Girls Hostel Block ${i + 1}` })),
    }
  },
  {
    id: 'iiit_bangalore',
    name: 'International Institute of Information Technology - [IIIT-B], Bangalore',
    city: 'Bangalore, Karnataka',
    hostels: {
      male: [{ id: 'bh1', label: 'Bhaskara' }, { id: 'bh2', label: 'Lilavati' }],
      female: [{ id: 'gh1', label: 'Visvesvaraya' }],
    }
  },
  {
    id: 'bit_mesra',
    name: 'Birla Institute of Technology - [BIT Mesra], Ranchi',
    city: 'Ranchi, Jharkhand',
    hostels: {
      male: Array.from({ length: 12 }, (_, i) => ({ id: `bh_${i + 1}`, label: `Hostel ${i + 1}` })),
      female: Array.from({ length: 3 }, (_, i) => ({ id: `gh_${i + 1}`, label: `Girls Hostel ${i + 1}` })),
    }
  },
  {
    id: 'vit_pune',
    name: 'Vishwakarma Institute of Technology, Pune',
    city: 'Pune, Maharashtra',
    hostels: {
      male: Array.from({ length: 4 }, (_, i) => ({ id: `bh_${i + 1}`, label: `VIT Boys Hostel ${i + 1}` })),
      female: Array.from({ length: 2 }, (_, i) => ({ id: `gh_${i + 1}`, label: `VIT Girls Hostel ${i + 1}` })),
    }
  },
  {
    id: 'vit_vellore',
    name: 'Vellore Institute of Technology - [VIT University], Vellore',
    city: 'Vellore, Tamil Nadu',
    hostels: {
      male: Array.from({ length: 18 }, (_, i) => String.fromCharCode(65 + i)).map(c => ({ id: `block_${c.toLowerCase()}`, label: `${c} Block` })),
      female: Array.from({ length: 7 }, (_, i) => String.fromCharCode(65 + i)).map(c => ({ id: `gblock_${c.toLowerCase()}`, label: `Ladies ${c} Block` })),
    }
  },
  {
    id: 'psg_coimbatore',
    name: 'P.S.G College of Technology - [PSGCT], Coimbatore',
    city: 'Coimbatore, Tamil Nadu',
    hostels: {
      male: Array.from({ length: 8 }, (_, i) => ({ id: `bh_${i + 1}`, label: `Hostel Block ${i + 1}` })),
      female: Array.from({ length: 3 }, (_, i) => ({ id: `gh_${i + 1}`, label: `Girls Hostel Block ${i + 1}` })),
    }
  },
  {
    id: 'rvce_bangalore',
    name: 'R V College of Engineering - [RVCE], Bangalore',
    city: 'Bangalore, Karnataka',
    hostels: {
      male: [{ id: 'cauvery', label: 'Cauvery Block' }, { id: 'sir_mv', label: 'Sir M. Visvesvaraya Block' }],
      female: [{ id: 'tunga', label: 'Tunga Block' }, { id: 'bhadra', label: 'Bhadra Block' }],
    }
  },
  {
    id: 'hbtu_kanpur',
    name: 'Harcourt Butler Technological University - [HBTU], Kanpur',
    city: 'Kanpur, Uttar Pradesh',
    hostels: {
      male: Array.from({ length: 6 }, (_, i) => ({ id: `bh_${i + 1}`, label: `West Campus Hostel ${i + 1}` })),
      female: Array.from({ length: 3 }, (_, i) => ({ id: `gh_${i + 1}`, label: `East Campus Girls Hostel ${i + 1}` })),
    }
  },
  {
    id: 'symbiosis_pune',
    name: 'Symbiosis International University - [SIU], Pune',
    city: 'Pune, Maharashtra',
    hostels: {
      male: Array.from({ length: 4 }, (_, i) => ({ id: `bh_${i + 1}`, label: `Hostel Wing ${i + 1}` })),
      female: Array.from({ length: 4 }, (_, i) => ({ id: `gh_${i + 1}`, label: `Girls Hostel Wing ${i + 1}` })),
    }
  },
  {
    id: 'bmsce_bangalore',
    name: 'BMS College of Engineering - [BMSCE], Bangalore',
    city: 'Bangalore, Karnataka',
    hostels: {
      male: Array.from({ length: 4 }, (_, i) => ({ id: `bh_${i + 1}`, label: `BMS Boys Hostel ${i + 1}` })),
      female: Array.from({ length: 2 }, (_, i) => ({ id: `gh_${i + 1}`, label: `BMS Girls Hostel ${i + 1}` })),
    }
  },
  {
    id: 'snu_chennai',
    name: 'Shiv Nadar University, Chennai',
    city: 'Chennai, Tamil Nadu',
    hostels: {
      male: Array.from({ length: 3 }, (_, i) => ({ id: `bh_${i + 1}`, label: `SNU Boys Hostel ${i + 1}` })),
      female: Array.from({ length: 2 }, (_, i) => ({ id: `gh_${i + 1}`, label: `SNU Girls Hostel ${i + 1}` })),
    }
  },
  {
    id: 'amrita_coimbatore',
    name: 'Amrita Vishwa Vidyapeetham, Coimbatore',
    city: 'Coimbatore, Tamil Nadu',
    hostels: {
      male: Array.from({ length: 8 }, (_, i) => ({ id: `bh_${i + 1}`, label: `Asramam Block ${i + 1}` })),
      female: Array.from({ length: 4 }, (_, i) => ({ id: `gh_${i + 1}`, label: `Pranavam Block ${i + 1}` })),
    }
  },
  {
    id: 'srm_chennai',
    name: 'SRM Institute of Science and Technology - [SRMIST], Chennai',
    city: 'Chennai, Tamil Nadu',
    hostels: {
      male: Array.from({ length: 14 }, (_, i) => ({ id: `bh_${i + 1}`, label: `Boys Hostel Block ${i + 1}` })),
      female: Array.from({ length: 7 }, (_, i) => ({ id: `gh_${i + 1}`, label: `Girls Hostel Block ${i + 1}` })),
    }
  },
  {
    id: 'cummins_pune',
    name: "MKSSS's Cummins College of Engineering for Women, Pune",
    city: 'Pune, Maharashtra',
    hostels: {
      male: [],
      female: Array.from({ length: 4 }, (_, i) => ({ id: `gh_${i + 1}`, label: `Cummins GH ${i + 1}` })),
    }
  },
  {
    id: 'vnr_vjiet_hyd',
    name: 'Vallurupalli Nageswara Rao Vignana Jyothi Institute of Engineering and Technology - [VNR VJIET], Hyderabad',
    city: 'Hyderabad, Telangana',
    hostels: {
      male: [{ id: 'bh1', label: 'VNR Boys Hostel 1' }, { id: 'bh2', label: 'VNR Boys Hostel 2' }],
      female: [{ id: 'gh1', label: 'VNR Girls Hostel 1' }],
    }
  },
  {
    id: 'msrit_bangalore',
    name: 'Ramaiah Institute of Technology - [RIT], Bangalore',
    city: 'Bangalore, Karnataka',
    hostels: {
      male: [{ id: 'bh1', label: 'MSR Boys Hostel 1' }, { id: 'bh2', label: 'MSR Boys Hostel 2' }],
      female: [{ id: 'gh1', label: 'MSR Girls Hostel 1' }],
    }
  },
  {
    id: 'pes_bangalore',
    name: 'PES University - [PESU], Bangalore',
    city: 'Bangalore, Karnataka',
    hostels: {
      male: [{ id: 'bh1', label: 'PES Boys Hostel 1' }, { id: 'bh2', label: 'PES Boys Hostel 2' }],
      female: [{ id: 'gh1', label: 'PES Girls Hostel 1' }],
    }
  },
  {
    id: 'cit_chennai',
    name: 'Chennai Institute of Technology - [CIT], Chennai',
    city: 'Chennai, Tamil Nadu',
    hostels: {
      male: [{ id: 'bh1', label: 'CIT Boys Hostel 1' }],
      female: [{ id: 'gh1', label: 'CIT Girls Hostel 1' }],
    }
  },
  {
    id: 'cbit_hyd',
    name: 'Chaitanya Bharathi Institute of Technology - [CBIT], Hyderabad',
    city: 'Hyderabad, Telangana',
    hostels: {
      male: [{ id: 'bh1', label: 'CBIT Boys Hostel 1' }],
      female: [{ id: 'gh1', label: 'CBIT Girls Hostel 1' }],
    }
  },
  {
    id: 'rec_chennai',
    name: 'Rajalakshmi Engineering College - [REC], Chennai',
    city: 'Chennai, Tamil Nadu',
    hostels: {
      male: [{ id: 'bh1', label: 'REC Boys Hostel 1' }],
      female: [{ id: 'gh1', label: 'REC Girls Hostel 1' }],
    }
  },
  {
    id: 'djsce_mumbai',
    name: 'Dwarkadas J Sanghvi College of Engineering - [DJSCE], Mumbai',
    city: 'Mumbai, Maharashtra',
    hostels: {
      male: [{ id: 'bh1', label: 'DJS Boys Hostel' }],
      female: [{ id: 'gh1', label: 'DJS Girls Hostel' }],
    }
  },
  {
    id: 'nmims_mumbai',
    name: 'Narsee Monjee Institute of Management Studies - [NMIMS Deemed to be University], Mumbai',
    city: 'Mumbai, Maharashtra',
    hostels: {
      male: [{ id: 'bh1', label: 'NMIMS Boys Hostel' }],
      female: [{ id: 'gh1', label: 'NMIMS Girls Hostel' }],
    }
  },
  {
    id: 'mahe_manipal',
    name: 'Manipal Academy of Higher Education - [MAHE], Manipal',
    city: 'Manipal, Karnataka',
    hostels: {
      male: Array.from({ length: 10 }, (_, i) => ({ id: `bh_${i + 1}`, label: `Block ${i + 1}` })),
      female: Array.from({ length: 5 }, (_, i) => ({ id: `gh_${i + 1}`, label: `Ladies Block ${i + 1}` })),
    }
  },
  {
    id: 'skcet_coimbatore',
    name: 'Sri Krishna College of Engineering and Technology - [SKCET], Coimbatore',
    city: 'Coimbatore, Tamil Nadu',
    hostels: {
      male: [{ id: 'bh1', label: 'SKCET Boys Hostel' }],
      female: [{ id: 'gh1', label: 'SKCET Girls Hostel' }],
    }
  },
  {
    id: 'msit_delhi',
    name: 'Maharaja Surajmal Institute of Technology - [MSIT], New Delhi',
    city: 'New Delhi, Delhi NCR',
    hostels: {
      male: [{ id: 'bh1', label: 'MSIT Boys Hostel' }],
      female: [{ id: 'gh1', label: 'MSIT Girls Hostel' }],
    }
  },
  {
    id: 'jiit_noida',
    name: 'Jaypee Institute of Information Technology University - [JIIT], Noida',
    city: 'Noida, Uttar Pradesh',
    hostels: {
      male: [{ id: 'bh1', label: 'JIIT Boys Hostel' }],
      female: [{ id: 'gh1', label: 'JIIT Girls Hostel' }],
    }
  },
  {
    id: 'i2it_pune',
    name: 'International Institute of Information Technology - [I²IT], Pune',
    city: 'Pune, Maharashtra',
    hostels: {
      male: [{ id: 'bh1', label: 'I2IT Boys Hostel' }],
      female: [{ id: 'gh1', label: 'I2IT Girls Hostel' }],
    }
  },
  {
    id: 'cmrit_bangalore',
    name: 'CMR Institute of Technology - [CMRIT], Bangalore',
    city: 'Bangalore, Karnataka',
    hostels: {
      male: [{ id: 'bh1', label: 'CMR Boys Hostel' }],
      female: [{ id: 'gh1', label: 'CMR Girls Hostel' }],
    }
  },
  {
    id: 'dypcoe_pune',
    name: 'D. Y. Patil College of Engineering, Pune',
    city: 'Pune, Maharashtra',
    hostels: {
      male: [{ id: 'bh1', label: 'DYP Boys Hostel' }],
      female: [{ id: 'gh1', label: 'DYP Girls Hostel' }],
    }
  },
  {
    id: 'sit_tumkur',
    name: 'Siddaganga Institute of Technology - [SIT], Tumkur',
    city: 'Tumkur, Karnataka',
    hostels: {
      male: [{ id: 'bh1', label: 'SIT Boys Hostel' }],
      female: [{ id: 'gh1', label: 'SIT Girls Hostel' }],
    }
  },
  {
    id: 'muj_jaipur',
    name: 'Manipal University - [MUJ], Jaipur',
    city: 'Jaipur, Rajasthan',
    hostels: {
      male: Array.from({ length: 6 }, (_, i) => ({ id: `bh_${i + 1}`, label: `Boys Block ${i + 1}` })),
      female: Array.from({ length: 3 }, (_, i) => ({ id: `gh_${i + 1}`, label: `Girls Block ${i + 1}` })),
    }
  },
  {
    id: 'pict_pune',
    name: 'Pune Institute of Computer Technology- [PICT], Pune',
    city: 'Pune, Maharashtra',
    hostels: {
      male: [{ id: 'bh1', label: 'PICT Boys Hostel' }],
      female: [{ id: 'gh1', label: 'PICT Girls Hostel' }],
    }
  },
  {
    id: 'dsce_bangalore',
    name: 'Dayananda Sagar College of Engineering - [DSCE], Bangalore',
    city: 'Bangalore, Karnataka',
    hostels: {
      male: [{ id: 'bh1', label: 'DSCE Boys Hostel' }],
      female: [{ id: 'gh1', label: 'DSCE Girls Hostel' }],
    }
  },
  {
    id: 'srm_amaravathi',
    name: 'SRM University, Amaravathi',
    city: 'Amaravathi, Andhra Pradesh',
    hostels: {
      male: [{ id: 'bh1', label: 'SRM Boys Hostel' }],
      female: [{ id: 'gh1', label: 'SRM Girls Hostel' }],
    }
  },
  {
    id: 'spit_mumbai',
    name: 'Sardar Patel Institute of Technology - [SPIT], Mumbai',
    city: 'Mumbai, Maharashtra',
    hostels: {
      male: [{ id: 'bh1', label: 'SPIT Boys Hostel' }],
      female: [{ id: 'gh1', label: 'SPIT Girls Hostel' }],
    }
  },
  {
    id: 'soa_bhubaneswar',
    name: "Siksha 'O' Anusandhan University - [SOA], Bhubaneswar",
    city: 'Bhubaneswar, Odisha',
    hostels: {
      male: [{ id: 'bh1', label: 'SOA Boys Hostel' }],
      female: [{ id: 'gh1', label: 'SOA Girls Hostel' }],
    }
  },
  {
    id: 'nmit_bangalore',
    name: 'Nitte Meenakshi Institute of Technology - [NMIT] (Deemed to be University), Bangalore',
    city: 'Bangalore, Karnataka',
    hostels: {
      male: [{ id: 'bh1', label: 'NMIT Boys Hostel' }],
      female: [{ id: 'gh1', label: 'NMIT Girls Hostel' }],
    }
  },
  {
    id: 'griet_hyd',
    name: 'Gokaraju Rangaraju Institute of Engineering and Technology -  [GRIET], Hyderabad',
    city: 'Hyderabad, Telangana',
    hostels: {
      male: [{ id: 'bh1', label: 'GRIET Boys Hostel' }],
      female: [{ id: 'gh1', label: 'GRIET Girls Hostel' }],
    }
  },
  {
    id: 'amity_noida',
    name: 'Amity University, Noida',
    city: 'Noida, Uttar Pradesh',
    hostels: {
      male: Array.from({ length: 5 }, (_, i) => ({ id: `bh_${i + 1}`, label: `Amity Boys Block ${i + 1}` })),
      female: Array.from({ length: 5 }, (_, i) => ({ id: `gh_${i + 1}`, label: `Amity Girls Block ${i + 1}` })),
    }
  },
  {
    id: 'psg_itech',
    name: 'PSG Institute of Technology and Applied Research - [PSG iTech], Coimbatore',
    city: 'Coimbatore, Tamil Nadu',
    hostels: {
      male: [{ id: 'bh1', label: 'PSG iTech Boys Hostel' }],
      female: [{ id: 'gh1', label: 'PSG iTech Girls Hostel' }],
    }
  },
  {
    id: 'mmmut_gorakhpur',
    name: 'Madan Mohan Malaviya University of Technology - [MMMUT], Gorakhpur',
    city: 'Gorakhpur, Uttar Pradesh',
    hostels: {
      male: [{ id: 'bh1', label: 'MMMUT Boys Hostel' }],
      female: [{ id: 'gh1', label: 'MMMUT Girls Hostel' }],
    }
  },
  {
    id: 'upes_dehradun',
    name: 'UPES, Dehradun',
    city: 'Dehradun, Uttarakhand',
    hostels: {
      male: [{ id: 'bh1', label: 'UPES Boys Hostel' }],
      female: [{ id: 'gh1', label: 'UPES Girls Hostel' }],
    }
  },
  {
    id: 'kct_coimbatore',
    name: 'Kumaraguru College of Technology - [KCT], Coimbatore',
    city: 'Coimbatore, Tamil Nadu',
    hostels: {
      male: [{ id: 'bh1', label: 'KCT Boys Hostel' }],
      female: [{ id: 'gh1', label: 'KCT Girls Hostel' }],
    }
  },
  {
    id: 'pccoe_pune',
    name: 'Pimpri Chinchwad College of Engineering - [PCCOE], Pune',
    city: 'Pune, Maharashtra',
    hostels: {
      male: [{ id: 'bh1', label: 'PCCOE Boys Hostel' }],
      female: [{ id: 'gh1', label: 'PCCOE Girls Hostel' }],
    }
  },
  {
    id: 'aissms_ioit',
    name: 'AISSMS Institute of Information Technology, Pune',
    city: 'Pune, Maharashtra',
    hostels: {
      male: [{ id: 'bh1', label: 'AISSMS Boys Hostel' }],
      female: [{ id: 'gh1', label: 'AISSMS Girls Hostel' }],
    }
  },
  {
    id: 'pdeu_gandhinagar',
    name: 'Pandit Deendayal Energy University - [PDEU], Gandhinagar',
    city: 'Gandhinagar, Gujarat',
    hostels: {
      male: [{ id: 'bh1', label: 'PDEU Boys Hostel' }],
      female: [{ id: 'gh1', label: 'PDEU Girls Hostel' }],
    }
  },
  {
    id: 'aissms_coe',
    name: 'AISSMS College of Engineering - [AISSMSCOE], Pune',
    city: 'Pune, Maharashtra',
    hostels: {
      male: [{ id: 'bh1', label: 'AISSMS COE Boys Hostel' }],
      female: [{ id: 'gh1', label: 'AISSMS COE Girls Hostel' }],
    }
  },
  {
    id: 'gayatri_vizag',
    name: 'Gayatri Vidya Parishad College of Engineering, Visakhapatnam',
    city: 'Visakhapatnam, Andhra Pradesh',
    hostels: {
      male: [{ id: 'bh1', label: 'GVP Boys Hostel' }],
      female: [{ id: 'gh1', label: 'GVP Girls Hostel' }],
    }
  },
  {
    id: 'woxsen_hyd',
    name: 'Woxsen University, Hyderabad',
    city: 'Hyderabad, Telangana',
    hostels: {
      male: [{ id: 'bh1', label: 'Woxsen Boys Hostel' }],
      female: [{ id: 'gh1', label: 'Woxsen Girls Hostel' }],
    }
  },
  {
    id: 'cvr_rangareddy',
    name: 'CVR College of Engineering, Ibrahimpatnam, Rangareddy',
    city: 'Rangareddy, Telangana',
    hostels: {
      male: [{ id: 'bh1', label: 'CVR Boys Hostel' }],
      female: [{ id: 'gh1', label: 'CVR Girls Hostel' }],
    }
  },
  {
    id: 'nhce_bangalore',
    name: 'New Horizon College of Engineering - [NHCE], Bangalore',
    city: 'Bangalore, Karnataka',
    hostels: {
      male: [{ id: 'bh1', label: 'NHCE Boys Hostel' }],
      female: [{ id: 'gh1', label: 'NHCE Girls Hostel' }],
    }
  },
  {
    id: 'ycce_nagpur',
    name: 'Yeshwantrao Chavan College of Engineering - [YCCE], Nagpur',
    city: 'Nagpur, Maharashtra',
    hostels: {
      male: [{ id: 'bh1', label: 'YCCE Boys Hostel' }],
      female: [{ id: 'gh1', label: 'YCCE Girls Hostel' }],
    }
  },
  {
    id: 'viit_pune',
    name: 'Vishwakarma Institute of Information Technology - [VIIT], Pune',
    city: 'Pune, Maharashtra',
    hostels: {
      male: [{ id: 'bh1', label: 'VIIT Boys Hostel' }],
      female: [{ id: 'gh1', label: 'VIIT Girls Hostel' }],
    }
  },
  {
    id: 'tsec_mumbai',
    name: 'Thadomal Shahani Engineering College - [TSEC], Mumbai',
    city: 'Mumbai, Maharashtra',
    hostels: {
      male: [{ id: 'bh1', label: 'TSEC Boys Hostel' }],
      female: [{ id: 'gh1', label: 'TSEC Girls Hostel' }],
    }
  },
  {
    id: 'rec_rit_chennai',
    name: 'Rajalakshmi Institute of Technology - [RIT], Chennai',
    city: 'Chennai, Tamil Nadu',
    hostels: {
      male: [{ id: 'bh1', label: 'RIT Boys Hostel' }],
      female: [{ id: 'gh1', label: 'RIT Girls Hostel' }],
    }
  },
  {
    id: 'svecw_bhimavaram',
    name: 'Shri Vishnu Engineering College for Women - [SVECW], Bhimavaram',
    city: 'Bhimavaram, Andhra Pradesh',
    hostels: {
      male: [],
      female: [{ id: 'gh1', label: 'SVECW Girls Hostel' }],
    }
  },
  {
    id: 'gnits_hyd',
    name: 'G Narayanamma Institute of Technology and Science - [GNITS], Hyderabad',
    city: 'Hyderabad, Telangana',
    hostels: {
      male: [],
      female: [{ id: 'gh1', label: 'GNITS Girls Hostel' }],
    }
  },
  {
    id: 'bit_bangalore',
    name: 'Bangalore Institute of Technology - [BIT], Bangalore',
    city: 'Bangalore, Karnataka',
    hostels: {
      male: [{ id: 'bh1', label: 'BIT Boys Hostel' }],
      female: [{ id: 'gh1', label: 'BIT Girls Hostel' }],
    }
  },
  {
    id: 'vasavi_hyd',
    name: 'Vasavi College of Engineering, Hyderabad',
    city: 'Hyderabad, Telangana',
    hostels: {
      male: [{ id: 'bh1', label: 'Vasavi Boys Hostel' }],
      female: [{ id: 'gh1', label: 'Vasavi Girls Hostel' }],
    }
  },
  {
    id: 'vardhaman_hyd',
    name: 'Vardhaman College of Engineering - [VCE], Hyderabad',
    city: 'Hyderabad, Telangana',
    hostels: {
      male: [{ id: 'bh1', label: 'Vardhaman Boys Hostel' }],
      female: [{ id: 'gh1', label: 'Vardhaman Girls Hostel' }],
    }
  },
  {
    id: 'karpagam_coimbatore',
    name: 'Karpagam College of Engineering - [KCE], Coimbatore',
    city: 'Coimbatore, Tamil Nadu',
    hostels: {
      male: [{ id: 'bh1', label: 'Karpagam Boys Hostel' }],
      female: [{ id: 'gh1', label: 'Karpagam Girls Hostel' }],
    }
  },
  {
    id: 'dypit_pimpri',
    name: 'Dr. D. Y. Patil Institute of Technology - [DYPIT] Pimpri, Pune',
    city: 'Pune, Maharashtra',
    hostels: {
      male: [{ id: 'bh1', label: 'DYPIT Boys Hostel' }],
      female: [{ id: 'gh1', label: 'DYPIT Girls Hostel' }],
    }
  },
  {
    id: 'rvr_jc_guntur',
    name: 'RVR and JC College of Engineering, Guntur',
    city: 'Guntur, Andhra Pradesh',
    hostels: {
      male: [{ id: 'bh1', label: 'RVR Boys Hostel' }],
      female: [{ id: 'gh1', label: 'RVR Girls Hostel' }],
    }
  },
  {
    id: 'kmit_hyd',
    name: 'Keshav Memorial Institute of Technology - [KMIT], Hyderabad',
    city: 'Hyderabad, Telangana',
    hostels: {
      male: [{ id: 'bh1', label: 'KMIT Boys Hostel' }],
      female: [{ id: 'gh1', label: 'KMIT Girls Hostel' }],
    }
  },
  {
    id: 'mait_delhi',
    name: 'Maharaja Agrasen Institute of Technology - [MAIT], New Delhi',
    city: 'New Delhi, Delhi NCR',
    hostels: {
      male: [{ id: 'bh1', label: 'MAIT Boys Hostel' }],
      female: [{ id: 'gh1', label: 'MAIT Girls Hostel' }],
    }
  },
  {
    id: 'vesit_mumbai',
    name: 'Vivekanand Education Society Institute of Technology - [VESIT], Mumbai',
    city: 'Mumbai, Maharashtra',
    hostels: {
      male: [{ id: 'bh1', label: 'VESIT Boys Hostel' }],
      female: [{ id: 'gh1', label: 'VESIT Girls Hostel' }],
    }
  },
  {
    id: 'rscoe_pune',
    name: "JSPM'S Rajarshi Shahu College of Engineering - [RSCOE] Tathawade, Pune",
    city: 'Pune, Maharashtra',
    hostels: {
      male: [{ id: 'bh1', label: 'RSCOE Boys Hostel' }],
      female: [{ id: 'gh1', label: 'RSCOE Girls Hostel' }],
    }
  },
  {
    id: 'mit_chennai',
    name: 'Madras Institute of Technology- [MIT], Chennai',
    city: 'Chennai, Tamil Nadu',
    hostels: {
      male: [{ id: 'bh1', label: 'MIT Boys Hostel' }],
      female: [{ id: 'gh1', label: 'MIT Girls Hostel' }],
    }
  },
  {
    id: 'jssstu_mysore',
    name: 'JSS Science and Technology University -[JSSSTU], Mysore',
    city: 'Mysore, Karnataka',
    hostels: {
      male: [{ id: 'bh1', label: 'JSSSTU Boys Hostel' }],
      female: [{ id: 'gh1', label: 'JSSSTU Girls Hostel' }],
    }
  },
  {
    id: 'nerist_itanagar',
    name: 'North Eastern Regional Institute of Science and Technology - [NERIST], Itanagar',
    city: 'Itanagar, Arunachal Pradesh',
    hostels: {
      male: [{ id: 'bh1', label: 'NERIST Boys Hostel' }],
      female: [{ id: 'gh1', label: 'NERIST Girls Hostel' }],
    }
  },
  {
    id: 'chandigarh_univ',
    name: 'Chandigarh University - [CU], Mohali',
    city: 'Mohali, Punjab',
    hostels: {
      male: Array.from({ length: 6 }, (_, i) => ({ id: `bh_${i + 1}`, label: `CU Boys Hostel ${i + 1}` })),
      female: Array.from({ length: 4 }, (_, i) => ({ id: `gh_${i + 1}`, label: `CU Girls Hostel ${i + 1}` })),
    }
  },
  {
    id: 'mmcoe_pune',
    name: "Marathwada Mitra Mandal's College of Engineering, Pune",
    city: 'Pune, Maharashtra',
    hostels: {
      male: [{ id: 'bh1', label: 'MMCOE Boys Hostel' }],
      female: [{ id: 'gh1', label: 'MMCOE Girls Hostel' }],
    }
  },
  {
    id: 'svce_chennai',
    name: 'Sri Venkateswara College of Engineering - [SVCE], Sriperumbudur',
    city: 'Sriperumbudur, Tamil Nadu',
    hostels: {
      male: [{ id: 'bh1', label: 'SVCE Boys Hostel' }],
      female: [{ id: 'gh1', label: 'SVCE Girls Hostel' }],
    }
  },
  {
    id: 'kle_hubli',
    name: 'KLE Technological University, Hubli',
    city: 'Hubli, Karnataka',
    hostels: {
      male: [{ id: 'bh1', label: 'KLE Boys Hostel' }],
      female: [{ id: 'gh1', label: 'KLE Girls Hostel' }],
    }
  },
  {
    id: 'fcrit_navi_mumbai',
    name: 'Fr. C. Rodrigues Institute of Technology - [FCRIT], Navi Mumbai',
    city: 'Navi Mumbai, Maharashtra',
    hostels: {
      male: [{ id: 'bh1', label: 'FCRIT Boys Hostel' }],
      female: [{ id: 'gh1', label: 'FCRIT Girls Hostel' }],
    }
  },
  {
    id: 'nie_mysore',
    name: 'The National Institute of Engineering - [NIE], Mysore',
    city: 'Mysore, Karnataka',
    hostels: {
      male: [{ id: 'bh1', label: 'NIE Boys Hostel' }],
      female: [{ id: 'gh1', label: 'NIE Girls Hostel' }],
    }
  },
  {
    id: 'ict_mumbai',
    name: 'Institute of Chemical Technology - [ICT], Mumbai',
    city: 'Mumbai, Maharashtra',
    hostels: {
      male: [{ id: 'bh1', label: 'ICT Boys Hostel' }],
      female: [{ id: 'gh1', label: 'ICT Girls Hostel' }],
    }
  },
  {
    id: 'crce_mumbai',
    name: 'Fr. Conceicao Rodrigues College of Engineering - [CRCE], Mumbai',
    city: 'Mumbai, Maharashtra',
    hostels: {
      male: [{ id: 'bh1', label: 'CRCE Boys Hostel' }],
      female: [{ id: 'gh1', label: 'CRCE Girls Hostel' }],
    }
  },
  {
    id: 'sliet_sangrur',
    name: 'Sant Longowal Institute of Engineering and Technology - [SLIET], Sangrur',
    city: 'Sangrur, Punjab',
    hostels: {
      male: [{ id: 'bh1', label: 'SLIET Boys Hostel' }],
      female: [{ id: 'gh1', label: 'SLIET Girls Hostel' }],
    }
  },
  {
    id: 'parul_univ',
    name: 'Parul University, Vadodara',
    city: 'Vadodara, Gujarat',
    hostels: {
      male: Array.from({ length: 5 }, (_, i) => ({ id: `bh_${i + 1}`, label: `Parul Boys Hostel ${i + 1}` })),
      female: Array.from({ length: 4 }, (_, i) => ({ id: `gh_${i + 1}`, label: `Parul Girls Hostel ${i + 1}` })),
    }
  },
  {
    id: 'srkr_bhimavaram',
    name: 'Sagi Ramakrishnam Raju Engineering College - [SRKR ], Bhimavaram',
    city: 'Bhimavaram, Andhra Pradesh',
    hostels: {
      male: [{ id: 'bh1', label: 'SRKR Boys Hostel' }],
      female: [{ id: 'gh1', label: 'SRKR Girls Hostel' }],
    }
  },
  {
    id: 'sdmcet_dharwad',
    name: 'SDM College of Engineering and Technology - [SDMCET], Dharwad',
    city: 'Dharwad, Karnataka',
    hostels: {
      male: [{ id: 'bh1', label: 'SDMCET Boys Hostel' }],
      female: [{ id: 'gh1', label: 'SDMCET Girls Hostel' }],
    }
  }
];

function getCollegeById(id) {
  return COLLEGES.find((c) => c.id === id) || null;
}

module.exports = { COLLEGES, getCollegeById };
