do $$
DECLARE
        query              text;
BEGIN
    FOR counter IN 0 .. 999 BY 1
    LOOP
	   query :=  'insert into users (name, password) values (''' || 800000 + counter || ''',''Secret_123!'')';
	   EXECUTE query;
    END LOOP;
end; $$