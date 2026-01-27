-- Seed data for RepTrack app
-- Single user
INSERT INTO users (id, isGuest, username, email, avatarUrl, currentWeight, height) VALUES (
  'IwKlDyJZLuTVJ1SwqlANU5vcgq22', 0, 'demo_user', 'demo@example.com', NULL, 75.0, 175.0
);

-- GDPR consent for user
INSERT INTO gdpr_consent (userId, isAccepted, acceptedAt) VALUES (
  'IwKlDyJZLuTVJ1SwqlANU5vcgq22', 1, strftime('%s','now') * 1000
);

-- Weight record
INSERT INTO weight_records (id, date, value) VALUES (
  'wr1', strftime('%s','now') * 1000, 75.0
);

-- Exercises (~20 across muscle groups)
INSERT INTO exercise (id, name, muscleGroup, type, iconUrl, isCustom) VALUES
('ex_chest_bench', 'Bench Press', 'CHEST', 'WEIGHT_REPS', NULL, 0),
('ex_chest_fly', 'Dumbbell Fly', 'CHEST', 'WEIGHT_REPS', NULL, 0),
('ex_back_row', 'Barbell Row', 'BACK', 'WEIGHT_REPS', NULL, 0),
('ex_back_pull', 'Pull-up', 'BACK', 'WEIGHT_REPS', NULL, 0),
('ex_legs_squat', 'Squat', 'LEGS', 'WEIGHT_REPS', NULL, 0),
('ex_legs_deadlift', 'Deadlift', 'LEGS', 'WEIGHT_REPS', NULL, 0),
('ex_arms_biceps', 'Biceps Curl', 'ARMS', 'WEIGHT_REPS', NULL, 0),
('ex_arms_triceps', 'Triceps Extension', 'ARMS', 'WEIGHT_REPS', NULL, 0),
('ex_abs_plank', 'Plank', 'ABS', 'TIME_DISTANCE', NULL, 0),
('ex_abs_crunch', 'Crunches', 'ABS', 'WEIGHT_REPS', NULL, 0),
('ex_cardio_run', 'Running', 'CARDIO', 'TIME_DISTANCE', NULL, 0),
('ex_cardio_bike', 'Cycling', 'CARDIO', 'TIME_DISTANCE', NULL, 0),
('ex_shoulders_press', 'Overhead Press', 'ARMS', 'WEIGHT_REPS', NULL, 0),
('ex_shoulders_lateral', 'Lateral Raise', 'ARMS', 'WEIGHT_REPS', NULL, 0),
('ex_legs_lunge', 'Lunges', 'LEGS', 'WEIGHT_REPS', NULL, 0),
('ex_back_latpulldown', 'Lat Pull-down', 'BACK', 'WEIGHT_REPS', NULL, 0),
('ex_chest_pushup', 'Push-up', 'CHEST', 'WEIGHT_REPS', NULL, 0),
('ex_arms_hammer', 'Hammer Curl', 'ARMS', 'WEIGHT_REPS', NULL, 0),
('ex_core_bike', 'Bicycle Crunch', 'ABS', 'WEIGHT_REPS', NULL, 0),
('ex_cardio_row', 'Rowing', 'CARDIO', 'TIME_DISTANCE', NULL, 0);

-- Two templates
INSERT INTO workout_templates (id, name, iconId) VALUES
('tmpl_full_body', 'Full Body', NULL),
('tmpl_push_day', 'Push Day', NULL);

-- Template -> exercises mapping (simple selections)
INSERT INTO template_exercises (templateId, exerciseId) VALUES
('tmpl_full_body','ex_chest_bench'),
('tmpl_full_body','ex_back_row'),
('tmpl_full_body','ex_legs_squat'),
('tmpl_full_body','ex_arms_biceps'),
('tmpl_full_body','ex_abs_plank'),
('tmpl_push_day','ex_chest_bench'),
('tmpl_push_day','ex_shoulders_press'),
('tmpl_push_day','ex_arms_triceps');

-- A couple of workout sessions for the demo user
INSERT INTO workout_sessions (id, userId, date, status, name, durationSeconds, comment) VALUES
('ws1', 'IwKlDyJZLuTVJ1SwqlANU5vcgq22', strftime('%s','now') * 1000, 'COMPLETED', 'Morning Full Body', 3600, 'Felt good'),
('ws2', 'IwKlDyJZLuTVJ1SwqlANU5vcgq22', strftime('%s','now','-7 days') * 1000, 'COMPLETED', 'Last Week Push', 2700, NULL);

-- Exercises recorded in sessions
INSERT INTO workout_exercises (id, workoutSessionId, exerciseId, restTimerSeconds) VALUES
('we1','ws1','ex_chest_bench',90),
('we2','ws1','ex_back_row',90),
('we3','ws1','ex_legs_squat',120),
('we4','ws2','ex_chest_bench',90),
('we5','ws2','ex_shoulders_press',90);

-- Sets for exercises (a few sets per exercise)
INSERT INTO workout_sets (id, workoutExerciseId, setOrder, weight, reps, isCompleted) VALUES
('ws1_we1_s1','we1',1,80.0,8,1),
('ws1_we1_s2','we1',2,80.0,8,1),
('ws1_we2_s1','we2',1,60.0,10,1),
('ws1_we3_s1','we3',1,100.0,5,1),
('ws2_we4_s1','we4',1,75.0,8,1),
('ws2_we5_s1','we5',1,40.0,10,1);

-- small comment: end of seed
