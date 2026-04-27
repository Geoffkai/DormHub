CREATE DATABASE IF NOT EXISTS dormhub;
USE dormhub;

CREATE TABLE room (
    room_number     INT(3)          PRIMARY KEY,
    room_type       VARCHAR(6)      NOT NULL CHECK (room_type IN ('Male', 'Female')),
    capacity        INT(1)          NOT NULL,
    current_occupancy INT(1)        NOT NULL DEFAULT 0,
    CONSTRAINT chk_occupancy CHECK (current_occupancy <= capacity)
);

CREATE TABLE resident (
    resident_id     INT(9)          PRIMARY KEY,
    last_name       VARCHAR(50)     NOT NULL,
    first_name      VARCHAR(50)     NOT NULL,
    contact_no      CHAR(11)        NOT NULL,
    year_level      INT(1)          NOT NULL,
    program         VARCHAR(20)     NOT NULL,
    move_in_date    DATE
);

CREATE TABLE room_assignments (
    assignment_id   INT(3)          PRIMARY KEY AUTO_INCREMENT,
    resident_id     INT(9)          NOT NULL,
    room_id         INT(3)          NOT NULL,
    date_assigned   DATE            NOT NULL,
    date_vacated    DATE,
    CONSTRAINT fk_ra_resident FOREIGN KEY (resident_id) REFERENCES resident(resident_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_ra_room FOREIGN KEY (room_id) REFERENCES room(room_number)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE payment (
    payment_id      INT(6)          PRIMARY KEY AUTO_INCREMENT,
    resident_id     INT(9)          NOT NULL,
    amount          DECIMAL(10,2)   NOT NULL,
    payment_date    DATE            NOT NULL,
    status          VARCHAR(6)      NOT NULL DEFAULT 'Unpaid' CHECK (status IN ('Paid', 'Unpaid')),
    CONSTRAINT fk_pay_resident FOREIGN KEY (resident_id) REFERENCES resident(resident_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE dorm_pass (
    pass_id         INT             PRIMARY KEY AUTO_INCREMENT,
    resident_id     INT(9)          NOT NULL,
    type            VARCHAR(50),
    reason          VARCHAR(50)     NOT NULL,
    destination     VARCHAR(50)     NOT NULL,
    date_applied    DATE            NOT NULL,
    status          VARCHAR(10)     NOT NULL DEFAULT 'Pending' CHECK (status IN ('Pending', 'Approved', 'Denied')),
    CONSTRAINT fk_dp_resident FOREIGN KEY (resident_id) REFERENCES resident(resident_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

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

-- ROOMS (5 rooms, male and female)
INSERT INTO room (room_number, room_type, capacity, current_occupancy) VALUES
(101, 'Male',   4, 4),
(102, 'Male',   4, 3),
(103, 'Female', 4, 4),
(104, 'Female', 4, 3),
(105, 'Male',   4, 2);

-- ROOM ASSIGNMENTS
-- Room 101 (Male, full)
INSERT INTO room_assignments (resident_id, room_id, date_assigned, date_vacated) VALUES
(202415006, 101, '2024-08-01', NULL),
(202415008, 101, '2024-08-02', NULL),
(202415010, 101, '2024-08-05', NULL),
(202215018, 101, '2022-08-02', NULL),

-- Room 102 (Male, 3/4)
(202515002, 102, '2025-08-01', NULL),
(202515004, 102, '2025-08-03', NULL),
(202215016, 102, '2022-08-01', NULL),

-- Room 103 (Female, full)
(202515001, 103, '2025-08-01', NULL),
(202515003, 103, '2025-08-03', NULL),
(202315013, 103, '2023-08-03', NULL),
(202215017, 103, '2022-08-01', NULL),

-- Room 104 (Female, 3/4)
(202515005, 104, '2025-08-04', NULL),
(202315011, 104, '2023-08-01', NULL),
(202315015, 104, '2023-08-04', NULL),

-- Room 105 (Male, 2/4) -- these two had previous assignments elsewhere
(202315012, 105, '2025-01-06', NULL),
(202215020, 105, '2025-01-06', NULL);

-- a couple past/vacated assignments for history
INSERT INTO room_assignments (resident_id, room_id, date_assigned, date_vacated) VALUES
(202315012, 103, '2023-08-01', '2025-01-05'),  -- Emilio was briefly in wrong room, reassigned
(202415009, 104, '2024-08-02', '2025-05-30');  -- Rina vacated end of last sem

-- PAYMENTS
INSERT INTO payment (resident_id, amount, payment_date, status) VALUES
-- paid up residents
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

-- unpaid / pending residents
(202515004, 3500.00, '2026-02-01', 'Unpaid'),
(202515005, 3500.00, '2026-02-01', 'Unpaid'),
(202315015, 3500.00, '2026-02-01', 'Unpaid'),
(202215016, 3500.00, '2026-02-01', 'Unpaid'),

-- some residents with payment history (prev semester)
(202415006, 3500.00, '2025-08-05', 'Paid'),
(202215018, 3500.00, '2025-08-07', 'Paid'),
(202315013, 3500.00, '2025-08-08', 'Paid');

-- DORM PASSES
INSERT INTO dorm_pass (resident_id, type, reason, destination, date_applied, status) VALUES
-- approved passes
(202515002, 'Overnight', 'Family visit',          'Palo, Leyte',       '2026-02-14', 'Approved'),
(202515001, 'Overnight', 'Medical appointment',   'Tacloban City',     '2026-02-20', 'Approved'),
(202415006, 'Overnight', 'Graduation ceremony',   'Ormoc City',        '2026-01-15', 'Approved'),
(202315011, 'Overnight', 'Family emergency',      'Baybay, Leyte',     '2026-02-01', 'Approved'),
(202215017, 'Day',       'Grocery run',           'Tacloban City',     '2026-03-01', 'Approved'),
(202415008, 'Day',       'Dentist appointment',   'Tacloban City',     '2026-03-05', 'Approved'),

-- pending passes
(202515003, 'Overnight', 'Family reunion',        'Carigara, Leyte',   '2026-03-28', 'Pending'),
(202515004, 'Day',       'Bank transaction',      'Tacloban City',     '2026-03-29', 'Pending'),
(202315015, 'Overnight', 'Sister\'s wedding',     'Maasin, Leyte',     '2026-03-30', 'Pending'),
(202215016, 'Day',       'Pickup package',        'Tacloban City',     '2026-04-01', 'Pending'),

-- denied passes
(202415010, 'Overnight', 'Beach trip with friends', 'Samar',           '2026-02-10', 'Denied'),
(202315012, 'Overnight', 'Concert',               'Cebu City',         '2026-03-01', 'Denied');
