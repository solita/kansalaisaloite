-- Support count data is denormalized to database to improve performance and make data available after supports are removed from the database
alter table initiative add column support_count_data text;