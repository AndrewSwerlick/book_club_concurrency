#---
# Excerpted from "Seven Concurrency Models in Seven Weeks",
# published by The Pragmatic Bookshelf.
# Copyrights apply to this code. It may not be used to create training material,
# courses, books, articles, and the like. Contact us if you are in doubt.
# We make no guarantees that this code is fit for any purpose.
# Visit http://www.pragmaticprogrammer.com/titles/pb7con for more book information.
#---
defmodule Parallel do
  def map(collection, fun) do
    parent = self()

    processes = Enum.map(collection, fn(e) ->
        spawn_link(fn() ->
            send(parent, {self(), fun.(e)})
          end)
      end)

    Enum.map(processes, fn(pid) ->
        receive do
          {^pid, result} -> result
        end
      end)
  end

  def reduce(collection, fun) when length(collection) <= 1 do
    IO.puts(List.first(collection))
  end

  def reduce(collection, fun) do
    parent = self()
    chunks = Enum.chunk(collection, 3, 3,[])

    processes = Enum.map(chunks, fn(chunk) ->
      spawn_link(fn() ->
        send(parent, {self(), Enum.reduce(chunk, fun)})
      end)
    end)

    results = Enum.map(processes, fn(pid) ->
      receive do
        {^pid, result} -> result
      end
    end)

    reduce(results, fun)
  end
end
