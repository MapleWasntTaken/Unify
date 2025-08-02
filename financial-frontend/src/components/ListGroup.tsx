function ListGroup(){

    const items = [
        'fart' ,
        'cum'
    ];




    return (
        <>
            <h1>fart</h1>
            {items.length===0?<p>No items</p>:null}
            <ul className="list-group">
                {items.map((item) => (
                    <li key={item}>{item}</li>
                ))}
            </ul>
        </>
    );
}
export default ListGroup;