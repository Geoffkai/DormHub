CREATE DATABASE IF NOT EXISTS dormhub;
USE dormhub;

CREATE TABLE IF NOT EXISTS room (
    room_number       INT(3)        PRIMARY KEY,
    room_type         VARCHAR(9)    NOT NULL CHECK (room_type IN ('Regular', 'Transient')),
    capacity          INT(1)        NOT NULL,
    current_occupancy INT(1)        NOT NULL DEFAULT 0,
    CONSTRAINT chk_occupancy CHECK (current_occupancy <= capacity)
);

CREATE TABLE IF NOT EXISTS resident (
    resident_id  INT(9)       PRIMARY KEY,
    last_name    VARCHAR(50)  NOT NULL,
    first_name   VARCHAR(50)  NOT NULL,
    contact_no   CHAR(11)     NOT NULL,
    year_level   INT(1)       NOT NULL,
    program      VARCHAR(20)  NOT NULL,
    move_in_date DATE
);

CREATE TABLE IF NOT EXISTS room_assignments (
    assignment_id INT(3)  PRIMARY KEY AUTO_INCREMENT,
    resident_id   INT(9)  NOT NULL,
    room_id       INT(3)  NOT NULL,
    date_assigned DATE    NOT NULL,
    date_vacated  DATE,
    CONSTRAINT fk_ra_resident FOREIGN KEY (resident_id) REFERENCES resident(resident_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_ra_room FOREIGN KEY (room_id) REFERENCES room(room_number)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS payment (
    payment_id   INT(6)         PRIMARY KEY AUTO_INCREMENT,
    resident_id  INT(9)         NOT NULL,
    amount       DECIMAL(10,2)  NOT NULL,
    payment_date DATE           NOT NULL,
    status       VARCHAR(6)     NOT NULL DEFAULT 'Unpaid' CHECK (status IN ('Paid', 'Unpaid')),
    CONSTRAINT fk_pay_resident FOREIGN KEY (resident_id) REFERENCES resident(resident_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS dorm_pass (
    pass_id      INT         PRIMARY KEY AUTO_INCREMENT,
    resident_id  INT(9)      NOT NULL,
    type         VARCHAR(50),
    reason       VARCHAR(50) NOT NULL,
    destination  VARCHAR(50) NOT NULL,
    date_applied DATE        NOT NULL,
    status       VARCHAR(10) NOT NULL DEFAULT 'Pending' CHECK (status IN ('Pending', 'Approved', 'Denied')),
    CONSTRAINT fk_dp_resident FOREIGN KEY (resident_id) REFERENCES resident(resident_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);