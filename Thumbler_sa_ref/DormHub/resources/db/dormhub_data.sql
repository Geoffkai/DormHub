USE dormhub;

INSERT INTO resident (resident_id, last_name, first_name, contact_no, year_level, program, move_in_date) VALUES
(202515001, 'Reyes',      'Maria',     '09171234501', 1, 'BALit',    '2025-08-01'),
(202515002, 'Santos',     'Juan',      '09181234502', 1, 'BSCS',     '2025-08-01'),
(202515003, 'Cruz',       'Angela',    '09191234503', 1, 'BSBio',    '2025-08-03'),
(202515004, 'Garcia',     'Paolo',     '09201234504', 1, 'BSEcon',   '2025-08-03'),
(202515005, 'Mendoza',    'Trisha',    '09211234505', 1, 'BSPsych',  '2025-08-04'),
(202415006, 'Villanueva', 'Carlos',    '09221234506', 2, 'BSAM',     '2024-08-01'),
(202415007, 'Fernandez',  'Lovely',    '09231234507', 2, 'BSA',      '2024-08-01'),
(202415008, 'Lopez',      'Marco',     '09241234508', 2, 'BSCS',     '2024-08-02'),
(202415009, 'Aquino',     'Rina',      '09251234509', 2, 'BAMA',     '2024-08-02'),
(202415010, 'Ramos',      'Dante',     '09261234510', 2, 'BSM',      '2024-08-05'),
(202315011, 'Castillo',   'Isabel',    '09271234511', 3, 'BSPolSci', '2023-08-01'),
(202315012, 'Torres',     'Emilio',    '09281234512', 3, 'BSBio',    '2023-08-01'),
(202315013, 'Flores',     'Camille',   '09291234513', 3, 'BSEcon',   '2023-08-03'),
(202315014, 'Navarro',    'Jerome',    '09301234514', 3, 'BSA',      '2023-08-03'),
(202315015, 'Dela Cruz',  'Anika',     '09311234515', 3, 'BSPsych',  '2023-08-04'),
(202215016, 'Bautista',   'Ramon',     '09321234516', 4, 'BSAM',     '2022-08-01'),
(202215017, 'Magno',      'Patricia',  '09331234517', 4, 'BALit',    '2022-08-01'),
(202215018, 'Ignacio',    'Luis',      '09341234518', 4, 'BSCS',     '2022-08-02'),
(202215019, 'Padilla',    'Sophia',    '09351234519', 4, 'BSM',      '2022-08-02'),
(202215020, 'Herrera',    'Miguel',    '09361234520', 4, 'BAMA',     '2022-08-05');

INSERT INTO room (room_number, room_type, capacity, current_occupancy) VALUES
(101, 'Regular',   4, 4),
(102, 'Regular',   4, 3),
(103, 'Transient', 4, 4),
(104, 'Transient', 4, 3),
(105, 'Regular',   4, 2);

INSERT INTO room_assignments (resident_id, room_id, date_assigned, date_vacated) VALUES
(202415006, 101, '2024-08-01', NULL),
(202415008, 101, '2024-08-02', NULL),
(202415010, 101, '2024-08-05', NULL),
(202215018, 101, '2022-08-02', NULL),
(202515002, 102, '2025-08-01', NULL),
(202515004, 102, '2025-08-03', NULL),
(202215016, 102, '2022-08-01', NULL),
(202515001, 103, '2025-08-01', NULL),
(202515003, 103, '2025-08-03', NULL),
(202315013, 103, '2023-08-03', NULL),
(202215017, 103, '2022-08-01', NULL),
(202515005, 104, '2025-08-04', NULL),
(202315011, 104, '2023-08-01', NULL),
(202315015, 104, '2023-08-04', NULL),
(202315012, 105, '2025-01-06', NULL),
(202215020, 105, '2025-01-06', NULL),
(202315012, 103, '2023-08-01', '2025-01-05'),
(202415009, 104, '2024-08-02', '2025-05-30');

INSERT INTO payment (resident_id, amount, payment_date, status) VALUES
(202415006, 3500.00, '2026-01-05', 'Paid'),
(202415008, 3500.00, '2026-01-03', 'Paid'),
(202415010, 3500.00, '2026-01-10', 'Paid'),
(202215018, 3500.00, '2026-01-07', 'Paid'),
(202515002, 3500.00, '2026-01-04', 'Paid'),
(202515001, 3500.00, '2026-01-02', 'Paid'),
(202515003, 3500.00, '2026-01-06', 'Paid'),
(202315013, 3500.00, '2026-01-08', 'Paid'),
(202215017, 3500.00, '2026-01-05', 'Paid'),
(202315011, 3500.00, '2026-01-09', 'Paid'),
(202515004, 3500.00, '2026-02-01', 'Unpaid'),
(202515005, 3500.00, '2026-02-01', 'Unpaid'),
(202315015, 3500.00, '2026-02-01', 'Unpaid'),
(202215016, 3500.00, '2026-02-01', 'Unpaid'),
(202415006, 3500.00, '2025-08-05', 'Paid'),
(202215018, 3500.00, '2025-08-07', 'Paid'),
(202315013, 3500.00, '2025-08-08', 'Paid');

INSERT INTO dorm_pass (resident_id, type, reason, destination, date_applied, status) VALUES
(202515002, 'Overnight', 'Family visit',           'Palo, Leyte',     '2026-02-14', 'Approved'),
(202515001, 'Overnight', 'Medical appointment',    'Tacloban City',   '2026-02-20', 'Approved'),
(202415006, 'Overnight', 'Graduation ceremony',    'Ormoc City',      '2026-01-15', 'Approved'),
(202315011, 'Overnight', 'Family emergency',       'Baybay, Leyte',   '2026-02-01', 'Approved'),
(202215017, 'Home Pass', 'Grocery run',            'Tacloban City',   '2026-03-01', 'Approved'),
(202415008, 'Home Pass', 'Dentist appointment',    'Tacloban City',   '2026-03-05', 'Approved'),
(202515003, 'Overnight', 'Family reunion',         'Carigara, Leyte', '2026-03-28', 'Pending'),
(202515004, 'Home Pass', 'Bank transaction',       'Tacloban City',   '2026-03-29', 'Pending'),
(202315015, 'Overnight', 'Sister''s wedding',      'Maasin, Leyte',   '2026-03-30', 'Pending'),
(202215016, 'Home Pass', 'Pickup package',         'Tacloban City',   '2026-04-01', 'Pending'),
(202415010, 'Overnight', 'Beach trip with friends','Samar',           '2026-02-10', 'Denied'),
(202315012, 'Overnight', 'Concert',                'Cebu City',       '2026-03-01', 'Denied');