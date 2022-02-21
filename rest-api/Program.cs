using System.Text.Json;
using System.Text.Json.Serialization;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);
builder.Services.AddDbContext<BombDb>(opt => opt.UseInMemoryDatabase("Bombs"));
builder.Services.AddDatabaseDeveloperPageExceptionFilter();
var app = builder.Build();

var detonationTimeInSeconds = 2;

app.MapGet("/bombs", async (BombDb db) =>
    await db.Bombs.ToListAsync());

app.MapGet("/bombs/detonated", async (BombDb db) =>
    await db.Bombs.Where(t => t.IsDetonated).ToListAsync());

app.MapGet("/bombs/{id}", async (int id, BombDb db) =>
    await db.Bombs.FindAsync(id)
        is Bomb bomb
        ? Results.Ok(bomb)
        : Results.NotFound());

app.MapPost("/bombs/phonenumber/{phonenumber}", async (String phonenumber, BombDb db) =>
{
    var bomb = new Bomb
    {
        Id = Random.Shared.Next(),
        PhoneNumber = phonenumber,
        IsDetonated = false,
        DetonationTime = null,
        WiresCut = ""
    };

    db.Bombs.Add(bomb);
    await db.SaveChangesAsync();

    return Results.Created($"/bombs/{bomb.Id}", bomb);
});

app.MapPut("/bombs/ignite/{phonenumber}", async (String phonenumber, BombDb db) =>
{
    var searchedBombs = fetchBombsWithPhoneNumber(phonenumber, db);
    var foundBomb = searchedBombs[0];
    foundBomb.DetonationTime = DateTime.Now.AddSeconds(detonationTimeInSeconds);
    db.Bombs.Update(foundBomb);
    await db.SaveChangesAsync();
    
    Console.WriteLine("Received phone call " + phonenumber + ", tick tock...");
    new Timer(async (e) =>
    {
        await Task.Delay(detonationTimeInSeconds * 1000);

        using (var scope = app.Services.CreateScope())
        {
            var db2 = scope.ServiceProvider.GetRequiredService<BombDb>();
            db2.Database.EnsureCreated();
            var bombsBeforeDetonation = fetchBombsWithPhoneNumber(phonenumber, db2);
            var bombToBeDetonated = bombsBeforeDetonation[0];
            bombToBeDetonated.IsDetonated = true;
            db2.Bombs.Update(bombToBeDetonated);
            await db2.SaveChangesAsync();    
        }
        
    }, null, 0, -1);

    return Results.Ok(foundBomb);
});

app.MapPut("/bombs/{id}/cutwire/{colour}", async (int id, String colour, BombDb db) =>
{
    var searchedBombs = fetchBombsWithId(id, db);
    var foundBomb = searchedBombs[0];

    var expectedColourFound = 
        colour == "yellow" ||
        colour == "green" ||
        colour == "blue";
    
    if (expectedColourFound) {
        if (foundBomb.WiresCut.Length == 0 && (colour == "yellow" || colour == "blue")) {
            foundBomb.IsDetonated = true;
            db.Bombs.Update(foundBomb);
            await db.SaveChangesAsync();
            return Results.Ok("Kaboom!");    
        }
        if (foundBomb.WiresCut.Contains("green") && !foundBomb.WiresCut.Contains("yellow") && colour == "blue") {
            foundBomb.IsDetonated = true;
            db.Bombs.Update(foundBomb);
            await db.SaveChangesAsync();
            return Results.Ok("Kaboom!");    
        }
        foundBomb.WiresCut += colour;
        db.Bombs.Update(foundBomb);
        await db.SaveChangesAsync();
        return Results.Ok("OK!");
    }

    return Results.BadRequest();
});

List<Bomb> fetchBombsWithPhoneNumber(String phoneNumber, BombDb db)
{
    return db.Bombs.Where(t => t.PhoneNumber.Equals(phoneNumber)).ToList();
}

List<Bomb> fetchBombsWithId(int id, BombDb db)
{
    return db.Bombs.Where(t => t.Id.Equals(id)).ToList();
}

app.MapPut("/bombs/{id}", async (int id, Bomb inputBomb, BombDb db) =>
{
    var bomb = await db.Bombs.FindAsync(id);

    if (bomb is null) return Results.NotFound();

    bomb.IsDetonated = inputBomb.IsDetonated;

    await db.SaveChangesAsync();

    return Results.NoContent();
});

app.MapDelete("/bombs/{id}", async (int id, BombDb db) =>
{
    if (await db.Bombs.FindAsync(id) is Bomb bomb)
    {
        db.Bombs.Remove(bomb);
        await db.SaveChangesAsync();
        return Results.Ok(bomb);
    }

    return Results.NotFound();
});

app.Run();

class Bomb
{
    public int Id { get; set; }
    public string PhoneNumber { get; set; }
    public string WiresCut { get; set; }
    public bool IsDetonated { get; set; }
    [JsonConverter(typeof(UnixDateTimeConverter))]
    public DateTime? DetonationTime { get; set; }
}

public class UnixDateTimeConverter : JsonConverter<DateTime>
{
    public override DateTime Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
    {
        return DateTime.UnixEpoch.AddSeconds(reader.GetInt64());
    }

    public override void Write(Utf8JsonWriter writer, DateTime value, JsonSerializerOptions options)
    {
        DateTimeOffset dateTimeOffset = value;
        writer.WriteStringValue(dateTimeOffset.ToUnixTimeMilliseconds().ToString());
    }
}


class BombDb : DbContext
{
    public BombDb(DbContextOptions<BombDb> options)
        : base(options) { }

    public DbSet<Bomb> Bombs => Set<Bomb>();
}